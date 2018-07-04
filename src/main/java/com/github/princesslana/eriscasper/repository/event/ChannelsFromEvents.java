package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.ChannelPinsUpdateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelPinsUpdateEventData;
import com.github.princesslana.eriscasper.data.event.ChannelUpdateEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.ImmutableChannel;
import com.github.princesslana.eriscasper.data.resource.UnavailableGuild;
import com.github.princesslana.eriscasper.repository.ChannelRepository;
import com.github.princesslana.eriscasper.repository.FunctionData;
import com.github.princesslana.eriscasper.rx.Maybes;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class ChannelsFromEvents implements ChannelRepository {

  private static final ChannelFunctionData<Guild> ADD_GUILD_CHANNELS_FUNCTION =
      ChannelFunctionData.of(
          (map, guild) -> {
            if (guild.getChannels().isEmpty()) {
              return ImmutableMap.copyOf(map);
            }
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
  private static final ChannelFunctionData<UnavailableGuild> REMOVE_GUILD_CHANNELS_FUNCTION =
      ChannelFunctionData.of(
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
  private static final ChannelFunctionData<Channel> ADD_CHANNEL_FUNCTION =
      ChannelFunctionData.of(
          (map, channel) -> {
            map.put(channel.getId(), channel);
            return ImmutableMap.copyOf(map);
          });
  private static final ChannelFunctionData<Channel> REMOVE_CHANNEL_FUNCTION =
      ChannelFunctionData.of(
          (map, channel) -> {
            map.remove(channel.getId());
            return ImmutableMap.<Snowflake, Channel>builder().putAll(map).build();
          });
  private static final ChannelFunctionData<ChannelPinsUpdateEventData>
      PINS_UPDATE_CHANNEL_FUNCTION =
          ChannelFunctionData.of(
              (map, channelPinsData) -> {
                Channel channel = map.get(channelPinsData.getChannelId());
                map.put(
                    channel.getId(),
                    ImmutableChannel.builder()
                        .from(channel)
                        .lastPinTimestamp(channelPinsData.getLastPinTimestamp())
                        .build());
                return ImmutableMap.copyOf(map);
              });
  private static final ChannelFunctionData<Channel> UPDATE_CHANNEL_FUNCTION =
      ChannelFunctionData.of(
          (map, channel) -> {
            map.replace(channel.getId(), channel);
            return ImmutableMap.copyOf(map);
          });

  private final ConnectableObservable<ImmutableMap<Snowflake, Channel>> channelWatcher;

  @SuppressWarnings("unchecked")
  public ChannelsFromEvents(Observable<Event> events) {
    // Merge all events which are meant to modify the channel by any means
    channelWatcher =
        Observable.mergeArray(
                process(GuildCreateEvent.class, ADD_GUILD_CHANNELS_FUNCTION, events),
                process(GuildDeleteEvent.class, REMOVE_GUILD_CHANNELS_FUNCTION, events),
                process(ChannelCreateEvent.class, ADD_CHANNEL_FUNCTION, events),
                process(ChannelDeleteEvent.class, REMOVE_CHANNEL_FUNCTION, events),
                process(ChannelPinsUpdateEvent.class, PINS_UPDATE_CHANNEL_FUNCTION, events),
                process(ChannelUpdateEvent.class, UPDATE_CHANNEL_FUNCTION, events))
            .scan(ImmutableMap.<Snowflake, Channel>of(), (map, function) -> function.apply(map))
            .replay(1);
    channelWatcher.connect();
  }

  private <X, Z extends Wrapper<X> & Event>
      Observable<Function<Map<Snowflake, Channel>, ImmutableMap<Snowflake, Channel>>> process(
          Class<Z> event, ChannelFunctionData<X> data, Observable<Event> eventObservable) {
    return eventObservable.ofType(event).map(Wrapper::unwrap).map(data);
  }

  @Override
  public Maybe<Channel> getChannel(Snowflake id) {
    return channelWatcher.firstElement().flatMap(map -> Maybes.fromNullable(map.get(id)));
  }

  @Override
  public Observable<Channel> getChannels() {
    return channelWatcher
        .firstElement()
        .flatMapObservable(map -> Observable.fromIterable(map.values()));
  }

  private static class ChannelFunctionData<X> extends FunctionData<Snowflake, Channel, X> {
    private ChannelFunctionData(
        BiFunction<Map<Snowflake, Channel>, X, ImmutableMap<Snowflake, Channel>> function) {
      super(function);
    }

    private static <X> ChannelFunctionData<X> of(
        BiFunction<Map<Snowflake, Channel>, X, ImmutableMap<Snowflake, Channel>> function) {
      return new ChannelFunctionData<>(function);
    }
  }
}
