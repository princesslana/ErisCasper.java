package com.github.princesslana.eriscasper.gateway;

import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.HelloEventData;
import com.github.princesslana.eriscasper.data.event.ReadyEvent;
import com.github.princesslana.eriscasper.data.gateway.ImmutableResumePayload;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.github.princesslana.eriscasper.rx.Singles;
import com.github.princesslana.eriscasper.rx.websocket.RxWebSocket;
import com.github.princesslana.eriscasper.rx.websocket.RxWebSocketEvent;
import com.google.common.base.Preconditions;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.operator.RateLimiterOperator;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateway {

  private static final Logger LOG = LoggerFactory.getLogger(Gateway.class);

  /**
   * @see <a href="https://discordapp.com/developers/docs/topics/gateway#sending-payloads">
   *     https://discordapp.com/developers/docs/topics/gateway#sending-payloads</a>
   */
  private static final int MAX_MESSAGE_SIZE = 4096;

  private static final String VERSION = "6";
  private static final String ENCODING = "json";

  private final RxWebSocket ws;
  private final Payloads payloads;

  private Optional<SequenceNumber> lastSeenSequenceNumber = Optional.empty();

  private Optional<SessionId> sessionId = Optional.empty();

  /**
   * @see <a href="https://discordapp.com/developers/docs/topics/gateway#rate-limiting">
   *     https://discordapp.com/developers/docs/topics/gateway#rate-limiting</a>
   */
  private final RateLimiter sendLimit =
      RateLimiter.of(
          "Gateway#sendLimit",
          RateLimiterConfig.custom()
              .limitRefreshPeriod(Duration.ofSeconds(60))
              .limitForPeriod(120)
              .timeoutDuration(Duration.ofSeconds(60))
              .build());

  /**
   * @see <a href="https://discordapp.com/developers/docs/topics/gateway#identifying">
   *     https://discordapp.com/developers/docs/topics/gateway#identifying</a>
   */
  private final RateLimiter identifyLimit =
      RateLimiter.of(
          "Gateway#identifyLimit",
          RateLimiterConfig.custom()
              .limitRefreshPeriod(Duration.ofSeconds(5))
              .limitForPeriod(1)
              .timeoutDuration(Duration.ofSeconds(60))
              .build());

  public Gateway(RxWebSocket ws, Payloads payloads) {
    this.ws = ws;
    this.payloads = payloads;
  }

  private boolean isResumable() {
    return sessionId.isPresent() && lastSeenSequenceNumber.isPresent();
  }

  private void setSessionId(SessionId sid) {
    this.sessionId = Optional.of(sid);
  }

  private void sequenceNumberSeen(Optional<SequenceNumber> seq) {
    seq.ifPresent(sid -> lastSeenSequenceNumber = Optional.of(sid));
  }

  private Consumer<RxWebSocketEvent> warnOnClosing(String url, Optional<ShardPayload> shard) {
    return evt -> {
      if (evt instanceof RxWebSocketEvent.Closing || evt instanceof RxWebSocketEvent.Closed) {
        LOG.warn("Websocket closing: url={}, shard={}, event={}", url, shard, evt);
      }
    };
  }

  public Observable<Event> connect(String url, BotToken token, Optional<ShardPayload> shard) {
    CompositeDisposable disposables = new CompositeDisposable();

    Observable<Payload> ps =
        ws.connect(String.format("%s?v=%s&encoding=%s", url, VERSION, ENCODING))
            .doOnNext(warnOnClosing(url, shard))
            .doFinally(disposables::dispose)
            .ofType(RxWebSocketEvent.StringMessage.class)
            .map(RxWebSocketEvent.StringMessage::getText)
            .flatMapMaybe(
                Singles.toMaybeAnd(
                    payloads::read, (s, t) -> LOG.warn("Error reading payload: {}", s, t)))
            .doOnNext(p -> sequenceNumberSeen(p.s()))
            .share();

    disposables.add(
        ps.filter(Payload.isOp(OpCode.HELLO))
            .flatMapCompletable(p -> heartbeat(ws, p))
            .subscribe());

    disposables.add(
        ps.filter(Payload.isOp(OpCode.HELLO))
            .flatMapCompletable(p -> isResumable() ? resume(ws, token) : identify(ws, token, shard))
            .subscribe());

    disposables.add(
        ps.flatMapMaybe(payloads::toEvent)
            .ofType(ReadyEvent.class)
            .map(r -> r.unwrap().getSessionId())
            .doOnNext(s -> setSessionId(SessionId.of(s)))
            .subscribe());

    return ps.flatMapMaybe(payloads::toEvent).doOnNext(e -> LOG.debug("Event: {}.", e));
  }

  private Completable send(RxWebSocket ws, Payload payload) {
    return payloads
        .writeToString(payload)
        .lift(RateLimiterOperator.of(sendLimit))
        .filter(s -> s.getBytes().length <= MAX_MESSAGE_SIZE)
        .doOnComplete(() -> LOG.warn("Payload rejected as too long: {}.", payload))
        .flatMapCompletable(ws::send)
        .doOnComplete(() -> LOG.debug("Sent: {}.", payload));
  }

  private Completable identify(RxWebSocket ws, BotToken token, Optional<ShardPayload> shard) {
    return Single.just(payloads.identify(token, shard))
        .lift(RateLimiterOperator.of(identifyLimit))
        .flatMapCompletable(p -> send(ws, p));
  }

  private Completable heartbeat(RxWebSocket ws, Payload hello) {
    return payloads
        .dataAs(hello, HelloEventData.class)
        .flatMapObservable(
            h -> Observable.interval(h.getHeartbeatInterval(), TimeUnit.MILLISECONDS))
        .flatMapCompletable(l -> send(ws, payloads.heartbeat(lastSeenSequenceNumber)));
  }

  private Completable resume(RxWebSocket ws, BotToken token) {
    Preconditions.checkState(sessionId.isPresent(), "Can not resume without a session id");
    Preconditions.checkState(
        lastSeenSequenceNumber.isPresent(), "Can not resume without a sequence number");

    return Single.just(
            ImmutableResumePayload.builder()
                .token(token.unwrap())
                .sessionId(sessionId.get().unwrap())
                .seq(lastSeenSequenceNumber.get().unwrap().longValue())
                .build())
        .map(payloads::resume)
        .flatMapCompletable(p -> send(ws, p));
  }

  public Completable execute(OpCode code, Object object) {
    return send(ws, payloads.createPayload(code, object));
  }

  public static Gateway create(OkHttpClient client, Payloads payloads) {
    return new Gateway(new RxWebSocket(client), payloads);
  }
}
