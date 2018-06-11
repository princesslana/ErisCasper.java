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
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Predicate;
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

  public ChannelsFromEvents(Observable<Event> events) {
    // Merge all events which are meant to modify the channel by any means
    channelWatcher =
        events
            .ofType(GuildCreateEvent.class)
            .map(GuildCreateEvent::unwrap)
            .map(ADD_GUILD_CHANNELS_FUNCTION)
            .mergeWith(
                events
                    .ofType(GuildDeleteEvent.class)
                    .map(GuildDeleteEvent::unwrap)
                    .map(REMOVE_GUILD_CHANNELS_FUNCTION))
            .mergeWith(
                events
                    .ofType(ChannelCreateEvent.class)
                    .map(ChannelCreateEvent::unwrap)
                    .map(ADD_CHANNEL_FUNCTION))
            .mergeWith(
                events
                    .ofType(ChannelDeleteEvent.class)
                    .map(ChannelDeleteEvent::unwrap)
                    .map(REMOVE_CHANNEL_FUNCTION))
            .mergeWith(
                events
                    .ofType(ChannelPinsUpdateEvent.class)
                    .map(ChannelPinsUpdateEvent::unwrap)
                    .map(PINS_UPDATE_CHANNEL_FUNCTION))
            .mergeWith(
                events
                    .ofType(ChannelUpdateEvent.class)
                    .map(ChannelUpdateEvent::unwrap)
                    .map(UPDATE_CHANNEL_FUNCTION))
            .scan(ImmutableMap.<Snowflake, Channel>of(), (map, function) -> function.apply(map))
            .doOnError(Throwable::printStackTrace)
            .replay(1);
    channelWatcher.connect();
  }

  @Override
  public Maybe<Channel> getChannel(@NonNull Snowflake id) {
    return channelWatcher.firstElement().flatMap(map -> Maybes.fromNullable(map.get(id)));
  }

  @Override
  public Single<ImmutableMap<Snowflake, Channel>> getChannels() {
    return channelWatcher.firstOrError();
  }

  @Override
  public Maybe<Channel> getGuildCategoryFromName(Snowflake guildId, String name) {
    return filterGuild(guildId, checkName(name))
        .filter(channel -> channel.getType() == 4)
        .firstElement();
  }

  @Override
  public Maybe<Channel> getGuildChannelFromName(
      Snowflake guildId, @Nullable Snowflake category, String name) {
    return filterGuild(guildId, checkName(name))
        .filter(checkCategory(category))
        .filter(channel -> (channel.getType() == 0 || channel.getType() == 2))
        .firstElement();
  }

  @Override
  public Observable<Channel> getGuildCategories(Snowflake guildId) {
    return filterGuild(guildId, channel -> channel.getType() == 4);
  }

  @Override
  public Observable<Channel> getGuildChannelsInCategory(
      Snowflake guildId, @Nullable Snowflake category) {
    return filterGuild(guildId, checkCategory(category));
  }

  @Override
  public Observable<Channel> filter(Predicate<Channel> channelPredicate) {
    return channelWatcher
        .firstElement()
        .flatMapObservable(map -> Observable.fromIterable(map.values()))
        .filter(channelPredicate);
  }

  @Override
  public Observable<Channel> filterGuild(Snowflake guildId, Predicate<Channel> channelPredicate) {
    return filter(channel -> guildId.equals(channel.getGuildId().orElse(null)))
        .filter(channelPredicate);
  }

  private Predicate<Channel> checkCategory(@Nullable Snowflake category) {
    return channel -> channel.getParentId().map(id -> id.equals(category)).orElse(category == null);
  }

  private Predicate<Channel> checkName(String name) {
    return channel -> name.equalsIgnoreCase(channel.getName().orElse(null));
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
