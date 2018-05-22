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
import com.github.princesslana.eriscasper.repository.GuildRepository;
import com.github.princesslana.eriscasper.rx.Maybes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuildsFromEvents implements GuildRepository {

  private final ConnectableObservable<ImmutableMap<Snowflake, Guild>> guildCache;
  private final ConnectableObservable<ImmutableMap<Snowflake, Channel>> channelCache;

  public GuildsFromEvents(Observable<Event> events) {
    ImmutableMap<Snowflake, Channel> emptyChannelMap = ImmutableMap.of();
    ImmutableMap<Snowflake, Guild> emptyGuildMap = ImmutableMap.of();

    PublishSubject<ImmutableList<Channel>> channelsAddSubject = PublishSubject.create();
    PublishSubject<ImmutableList<Snowflake>> channelsRemoveSubject = PublishSubject.create();

    Observable<ImmutableMap<Snowflake, Channel>> addableChannelCache =
        channelsAddSubject.scan(
            emptyChannelMap,
            (map, newChannelList) -> {
              ImmutableMap.Builder<Snowflake, Channel> newMapBuilder =
                  ImmutableMap.<Snowflake, Channel>builder().putAll(map);
              newChannelList.forEach(channel -> newMapBuilder.put(channel.getId(), channel));
              return newMapBuilder.build();
            });
    Observable<ImmutableMap<Snowflake, Channel>> removeableChannelCache =
        channelsRemoveSubject.scan(
            emptyChannelMap,
            (map, newChannelList) -> {
              Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
              newChannelList.forEach(mutableMap::remove);
              return ImmutableMap.<Snowflake, Channel>builder().putAll(mutableMap).build();
            });
    Observable<ImmutableMap<Snowflake, Channel>> channelCreateListener =
        events
            .ofType(ChannelCreateEvent.class)
            .map(ChannelCreateEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, channel) ->
                    ImmutableMap.<Snowflake, Channel>builder()
                        .put(channel.getId(), channel)
                        .build());
    Observable<ImmutableMap<Snowflake, Channel>> channelDeleteListener =
        events
            .ofType(ChannelDeleteEvent.class)
            .map(ChannelDeleteEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, channel) -> {
                  Map<Snowflake, Channel> mutableMap = new HashMap<>(map);
                  mutableMap.remove(channel.getId());
                  return ImmutableMap.<Snowflake, Channel>builder().putAll(mutableMap).build();
                });

    channelCache =
        addableChannelCache
            .mergeWith(removeableChannelCache)
            .mergeWith(channelCreateListener)
            .mergeWith(channelDeleteListener)
            .replay(1);
    channelCache.connect();

    Observable<ImmutableMap<Snowflake, Guild>> guildCreateListener =
        events
            .ofType(GuildCreateEvent.class)
            .flatMap(event -> Observable.just(event.unwrap()))
            .scan(
                emptyGuildMap,
                (map, guild) -> {
                  ImmutableList<Channel> newList =
                      ImmutableList.<Channel>builder()
                          .addAll(
                              guild
                                  .getChannels()
                                  .stream()
                                  .map(
                                      channel ->
                                          ImmutableChannel.builder()
                                              .from(channel)
                                              .guildId(guild.getId())
                                              .build())
                                  .collect(Collectors.toList()))
                          .build();
                  channelsAddSubject.onNext(newList);
                  return ImmutableMap.<Snowflake, Guild>builder()
                      .putAll(map)
                      .put(guild.getId(), guild)
                      .build();
                });
    Observable<ImmutableMap<Snowflake, Guild>> guildDeleteListener =
        events
            .ofType(GuildDeleteEvent.class)
            .flatMap(event -> Observable.just(event.unwrap()))
            .scan(
                emptyGuildMap,
                (map, guild) -> {
                  Map<Snowflake, Guild> mutableMap = new HashMap<>(map);
                  mutableMap.remove(guild.getId());
                  channelCache
                      .firstOrError()
                      .subscribe(
                          channelMap -> {
                            ImmutableList.Builder<Snowflake> channelList =
                                new ImmutableList.Builder<>();
                            channelMap
                                .values()
                                .forEach(
                                    channel -> {
                                      if (guild.getId().equals(channel.getGuildId().orElse(null))) {
                                        channelList.add(channel.getId());
                                      }
                                    });
                            channelsRemoveSubject.onNext(channelList.build());
                          })
                      .dispose();
                  return ImmutableMap.<Snowflake, Guild>builder().putAll(mutableMap).build();
                });

    guildCache = guildCreateListener.mergeWith(guildDeleteListener).replay(1);
    guildCache.connect();
  }

  @Override
  public Maybe<Guild> getGuild(Optional<Snowflake> id) {
    return Maybes.fromOptional(id)
        .flatMap(
            snowflake ->
                guildCache
                    .firstOrError()
                    .flatMapMaybe(guildMap -> Maybes.fromNullable(guildMap.get(snowflake))));
  }

  @Override
  public Maybe<Channel> getChannel(@NonNull Snowflake id) {
    return channelCache
        .firstOrError()
        .flatMapMaybe(channelMap -> Maybes.fromNullable(channelMap.get(id)));
  }
}
