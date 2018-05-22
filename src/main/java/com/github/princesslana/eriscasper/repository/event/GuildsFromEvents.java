package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.repository.GuildRepository;
import com.github.princesslana.eriscasper.rx.Maybes;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observables.ConnectableObservable;
import java.util.HashMap;
import java.util.Map;

public class GuildsFromEvents implements GuildRepository {

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
    ImmutableMap<Snowflake, Guild> emptyGuildMap = ImmutableMap.of();

    Observable<ImmutableMap<Snowflake, Guild>> guildCreateListener =
        eventObservable
            .ofType(GuildCreateEvent.class)
            .flatMap(event -> Observable.just(event.unwrap()))
            .scan(
                emptyGuildMap,
                (map, guild) -> {
                  Map<Snowflake, Guild> mutableMap = new HashMap<>(map);
                  mutableMap.put(guild.getId(), guild);
                  return ImmutableMap.copyOf(mutableMap);
                });
    Observable<ImmutableMap<Snowflake, Guild>> guildDeleteListener =
        eventObservable
            .ofType(GuildDeleteEvent.class)
            .flatMap(event -> Observable.just(event.unwrap()))
            .scan(
                emptyGuildMap,
                (map, guild) -> {
                  Map<Snowflake, Guild> mutableMap = new HashMap<>(map);
                  mutableMap.remove(guild.getId());
                  return ImmutableMap.copyOf(mutableMap);
                });

    return guildCreateListener.mergeWith(guildDeleteListener).replay(1);
  }

  private ConnectableObservable<ImmutableMap<Snowflake, Channel>> initChannelWatcher() {
    ImmutableMap<Snowflake, Channel> emptyChannelMap = ImmutableMap.of();

    Observable<ImmutableMap<Snowflake, Channel>> guildCreateWithChannelsListener =
        eventObservable
            .ofType(GuildCreateEvent.class)
            .map(GuildCreateEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, guild) -> {
                  if (guild.getChannels().isEmpty()) return map;
                  Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
                  guild.getChannels().forEach(channel -> mutableMap.put(channel.getId(), channel));
                  return ImmutableMap.copyOf(mutableMap);
                });
    Observable<ImmutableMap<Snowflake, Channel>> guildDeleteWithChannelsListener =
        eventObservable
            .ofType(GuildDeleteEvent.class)
            .map(GuildDeleteEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, guild) -> {
                  Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
                  Snowflake guildId = guild.getId();
                  mutableMap
                      .values()
                      .stream()
                      .filter(channel -> guildId.equals(channel.getGuildId().orElse(null)))
                      .map(Channel::getId)
                      .forEach(mutableMap::remove);
                  return ImmutableMap.copyOf(mutableMap);
                });
    Observable<ImmutableMap<Snowflake, Channel>> channelCreateListener =
        eventObservable
            .ofType(ChannelCreateEvent.class)
            .map(ChannelCreateEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, channel) -> {
                  Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
                  mutableMap.put(channel.getId(), channel);
                  return ImmutableMap.copyOf(mutableMap);
                });
    Observable<ImmutableMap<Snowflake, Channel>> channelDeleteListener =
        eventObservable
            .ofType(ChannelDeleteEvent.class)
            .map(ChannelDeleteEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, channel) -> {
                  Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
                  mutableMap.remove(channel.getId());
                  return ImmutableMap.<Snowflake, Channel>builder().putAll(mutableMap).build();
                });

    return guildCreateWithChannelsListener
        .mergeWith(guildDeleteWithChannelsListener)
        .mergeWith(channelCreateListener)
        .mergeWith(channelDeleteListener)
        .replay(1);
  }

  @Override
  public Maybe<Guild> getGuild(@NonNull Snowflake id) {
    return guildWatcher.cache().firstElement().flatMap(map -> Maybes.fromNullable(map.get(id)));
  }

  @Override
  public Maybe<Channel> getChannel(@NonNull Snowflake id) {
    return channelWatcher
        .cache()
        .firstElement()
        .flatMap(channelMap -> Maybes.fromNullable(channelMap.get(id)));
  }
}
