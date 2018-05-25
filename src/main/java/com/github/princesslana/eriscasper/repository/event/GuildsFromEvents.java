package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.ImmutableChannel;
import com.github.princesslana.eriscasper.data.resource.UnavailableGuild;
import com.github.princesslana.eriscasper.repository.GuildRepository;
import com.github.princesslana.eriscasper.rx.Maybes;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class GuildsFromEvents implements GuildRepository {

  private static final FunctionData<Snowflake, Guild, Guild> ADD_GUILD_FUNCTION =
      FunctionData.of(
          (map, guild) -> {
            map.put(guild.getId(), guild);
            return ImmutableMap.copyOf(map);
          });
  private static final FunctionData<Snowflake, Guild, UnavailableGuild> REMOVE_GUILD_FUNCTION =
      FunctionData.of(
          (map, guild) -> {
            map.remove(guild.getId());
            return ImmutableMap.copyOf(map);
          });
  private static final FunctionData<Snowflake, Channel, Guild> ADD_GUILD_CHANNELS_FUNCTION =
      FunctionData.of(
          (map, guild) -> {
            if (guild.getChannels().isEmpty()) return ImmutableMap.copyOf(map);
            Snowflake guildId = guild.getId();
            guild
                .getChannels()
                .forEach(
                    channel ->
                        map.put(
                            channel.getId(),
                            ImmutableChannel.builder().from(channel).guildId(guildId).build()));
            return ImmutableMap.copyOf(map);
          });
  private static final FunctionData<Snowflake, Channel, UnavailableGuild>
      REMOVE_GUILD_CHANNELS_FUNCTION =
          FunctionData.of(
              (map, guild) -> {
                Snowflake guildId = guild.getId();
                // to avoid ConcurrentModificationException
                Stack<Snowflake> toRemove = new Stack<>();
                map.values()
                    .stream()
                    .filter(channel -> guildId.equals(channel.getGuildId().orElse(null)))
                    .map(Channel::getId)
                    .forEach(toRemove::push);
                while (!toRemove.isEmpty()) {
                  map.remove(toRemove.pop());
                }
                return ImmutableMap.copyOf(map);
              });
  private static final FunctionData<Snowflake, Channel, Channel> ADD_CHANNEL_FUNCTION =
      FunctionData.of(
          (map, channel) -> {
            map.put(channel.getId(), channel);
            return ImmutableMap.copyOf(map);
          });
  private static final FunctionData<Snowflake, Channel, Channel> REMOVE_CHANNEL_FUNCTION =
      FunctionData.of(
          (map, channel) -> {
            map.remove(channel.getId());
            return ImmutableMap.<Snowflake, Channel>builder().putAll(map).build();
          });

  private final Observable<Event> eventObservable;
  private final ConnectableObservable<ImmutableMap<Snowflake, Guild>> guildWatcher;
  private final ConnectableObservable<ImmutableMap<Snowflake, Channel>> channelWatcher;

  public GuildsFromEvents(Observable<Event> eventObservable) {
    this.eventObservable = eventObservable;

    this.channelWatcher = initChannelWatcher();
    this.channelWatcher.connect();

    this.guildWatcher = initGuildWatcher();
    this.guildWatcher.connect();
  }

  private ConnectableObservable<ImmutableMap<Snowflake, Guild>> initGuildWatcher() {
    return generateGuildCreateListener()
        .map(ADD_GUILD_FUNCTION)
        .mergeWith(generateGuildDeleteListener().map(REMOVE_GUILD_FUNCTION))
        .scan(ImmutableMap.<Snowflake, Guild>of(), (map, function) -> function.apply(map))
        .doOnError(Throwable::printStackTrace)
        .replay(1);
  }

  private ConnectableObservable<ImmutableMap<Snowflake, Channel>> initChannelWatcher() {
    return generateGuildCreateListener()
        .map(ADD_GUILD_CHANNELS_FUNCTION)
        .mergeWith(generateGuildDeleteListener().map(REMOVE_GUILD_CHANNELS_FUNCTION))
        .mergeWith(
            eventObservable
                .ofType(ChannelCreateEvent.class)
                .map(ChannelCreateEvent::unwrap)
                .map(ADD_CHANNEL_FUNCTION))
        .mergeWith(
            eventObservable
                .ofType(ChannelDeleteEvent.class)
                .map(ChannelDeleteEvent::unwrap)
                .map(REMOVE_CHANNEL_FUNCTION))
        .scan(ImmutableMap.<Snowflake, Channel>of(), (map, function) -> function.apply(map))
        .doOnError(Throwable::printStackTrace)
        .replay(1);
  }

  // Used due to avoiding redundant code
  private Observable<Guild> generateGuildCreateListener() {
    return eventObservable.ofType(GuildCreateEvent.class).map(GuildCreateEvent::unwrap);
  }

  // Used due to avoiding redundant code
  private Observable<UnavailableGuild> generateGuildDeleteListener() {
    return eventObservable.ofType(GuildDeleteEvent.class).map(GuildDeleteEvent::unwrap);
  }

  @Override
  public Maybe<Guild> getGuild(@NonNull Snowflake id) {
    return guildWatcher.firstElement().flatMap(map -> Maybes.fromNullable(map.get(id)));
  }

  @Override
  public Maybe<Channel> getChannel(@NonNull Snowflake id) {
    return channelWatcher
        .firstElement()
        .flatMap(channelMap -> Maybes.fromNullable(channelMap.get(id)));
  }

  private static class FunctionData<X, Y, Z>
      implements Function<Z, Function<Map<X, Y>, ImmutableMap<X, Y>>> {

    private final BiFunction<Map<X, Y>, Z, ImmutableMap<X, Y>> mapBiFunction;

    private FunctionData(BiFunction<Map<X, Y>, Z, ImmutableMap<X, Y>> mapBiFunction) {
      this.mapBiFunction = mapBiFunction;
    }

    @Override
    public Function<Map<X, Y>, ImmutableMap<X, Y>> apply(Z value) {
      // pushes it into a new HashMap to reduce code redundancy just from the general pattern.
      return (map) -> mapBiFunction.apply(new HashMap<>(map), value);
    }

    private static <X, Y, Z> FunctionData<X, Y, Z> of(
        BiFunction<Map<X, Y>, Z, ImmutableMap<X, Y>> function) {
      return new FunctionData<>(function);
    }
  }
}
