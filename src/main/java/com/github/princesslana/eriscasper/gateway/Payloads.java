package com.github.princesslana.eriscasper.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.EventFactory;
import com.github.princesslana.eriscasper.data.gateway.IdentifyPayload;
import com.github.princesslana.eriscasper.data.gateway.ImmutableConnectionPropertiesPayload;
import com.github.princesslana.eriscasper.data.gateway.ImmutableIdentifyPayload;
import com.github.princesslana.eriscasper.data.gateway.ResumePayload;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.github.princesslana.eriscasper.data.immutable.Wrapped;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.rx.Maybes;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Optional;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Payloads {

  private static final Logger LOG = LoggerFactory.getLogger(Payloads.class);

  private ObjectMapper jackson;

  public Payloads(ObjectMapper jackson) {
    this.jackson = jackson;
  }

  public <T> Single<T> dataAs(Payload p, Class<T> clazz) {
    return p.d(jackson, clazz);
  }

  public Payload heartbeat(Optional<SequenceNumber> s) {
    return ImmutablePayload.builder().op(OpCode.HEARTBEAT).d(s.map(jackson::valueToTree)).build();
  }

  public Payload identify(BotToken token, Optional<ShardPayload> shard) {
    return identify(
        ImmutableIdentifyPayload.builder()
            .properties(
                ImmutableConnectionPropertiesPayload.builder()
                    .os(System.getProperty("os"))
                    .browser("ErisCasper.Java")
                    .device("ErisCasper.java")
                    .build())
            .token(token.unwrap())
            .shard(shard)
            .build());
  }

  public Payload identify(IdentifyPayload id) {
    return createPayload(OpCode.IDENTIFY, id);
  }

  public Single<Payload> read(String text) {
    return Single.fromCallable(() -> jackson.readValue(text, Payload.class));
  }

  public Payload resume(ResumePayload r) {
    return createPayload(OpCode.RESUME, r);
  }

  public Payload createPayload(OpCode code, Object o) {
    return ImmutablePayload.builder().op(code).d(jackson.valueToTree(o)).build();
  }

  public Maybe<Event> toEvent(Payload payload) {
    return Single.just(payload)
        .filter(Payload.isOp(OpCode.DISPATCH))
        .flatMap(p -> Maybes.fromOptional(p.t()))
        .map(et -> EventFactory.forType(et).create(payload.d().get()))
        .doOnError(t -> LOG.warn("Unable to convert payload to event: {}", payload, t))
        .onErrorComplete();
  }

  public Single<String> writeToString(Payload p) {
    return Single.fromCallable(() -> jackson.writeValueAsString(p));
  }

  @Value.Immutable
  @Wrapped
  public interface SessionIdWrapper extends Wrapper<String> {}
}
