package com.github.princesslana.eriscasper.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.EventFactory;
import com.github.princesslana.eriscasper.data.immutable.Wrapped;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.gateway.commands.Identify;
import com.github.princesslana.eriscasper.gateway.commands.ImmutableIdentify;
import com.github.princesslana.eriscasper.gateway.commands.RequestGuildMembers;
import com.github.princesslana.eriscasper.gateway.commands.Resume;
import com.github.princesslana.eriscasper.gateway.commands.UpdatePresence;
import com.github.princesslana.eriscasper.gateway.commands.UpdateVoiceState;
import com.github.princesslana.eriscasper.rx.Maybes;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.Nullable;
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

  public Payload identify(BotToken token, @Nullable Integer[] shard) {
    return identify(
        ImmutableIdentify.builder().token(token).shard(Optional.ofNullable(shard)).build());
  }

  public Payload identify(Identify id) {
    return ImmutablePayload.builder().op(OpCode.IDENTIFY).d(jackson.valueToTree(id)).build();
  }

  public Single<Payload> read(String text) {
    return Single.fromCallable(() -> jackson.readValue(text, Payload.class));
  }

  public Payload resume(Resume r) {
    return ImmutablePayload.builder().op(OpCode.RESUME).d(jackson.valueToTree(r)).build();
  }

  public Payload requestGuildMembers(RequestGuildMembers request) {
    return ImmutablePayload.builder()
        .op(OpCode.REQUEST_GUILD_MEMBERS)
        .d(jackson.valueToTree(request))
        .build();
  }

  public Payload updatePresence(UpdatePresence update) {
    return ImmutablePayload.builder()
        .op(OpCode.STATUS_UPDATE)
        .d(jackson.valueToTree(update))
        .build();
  }

  public Payload updateVoiceState(UpdateVoiceState update) {
    return ImmutablePayload.builder()
        .op(OpCode.VOICE_STATE_UPDATE)
        .d(jackson.valueToTree(update))
        .build();
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
