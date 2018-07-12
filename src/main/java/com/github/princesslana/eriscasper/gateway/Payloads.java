package com.github.princesslana.eriscasper.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.EventFactory;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.github.princesslana.eriscasper.data.immutable.Wrapped;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.gateway.commands.Identify;
import com.github.princesslana.eriscasper.gateway.commands.ImmutableIdentify;
import com.github.princesslana.eriscasper.gateway.commands.Resume;
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
    return identify(ImmutableIdentify.builder().token(token).shard(shard).build());
  }

  public Payload identify(Identify id) {
    return id.toPayload(jackson);
  }

  public Single<Payload> read(String text) {
    return Single.fromCallable(() -> jackson.readValue(text, Payload.class));
  }

  public Payload resume(Resume r) {
    return r.toPayload(jackson);
  }

  public Payload createPayload(OpCode code, Object item) {
    return ImmutablePayload.builder().op(code).d(jackson.valueToTree(item)).build();
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
