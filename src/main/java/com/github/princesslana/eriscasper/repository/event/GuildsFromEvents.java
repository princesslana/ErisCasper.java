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
import java.util.stream.Collectors;

public class GuildsFromEvents implements GuildRepository {

  private final Observable<Event> eventObservable;
  private final ConnectableObservable<ImmutableMap<Snowflake, Guild>> guildWatcher;
  private final ConnectableObservable<ImmutableMap<Snowflake, Channel>> channelWatcher;
  private final PublishSubject<ImmutableList<Channel>> channelsAddSubject;
  private final PublishSubject<ImmutableList<Snowflake>> channelsRemoveSubject;

  public GuildsFromEvents(Observable<Event> eventObservable) {
    this.eventObservable = eventObservable;

    this.channelsAddSubject = PublishSubject.create();
    this.channelsRemoveSubject = PublishSubject.create();

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
        eventObservable
            .ofType(GuildDeleteEvent.class)
            .flatMap(event -> Observable.just(event.unwrap()))
            .scan(
                emptyGuildMap,
                (map, guild) -> {
                  Map<Snowflake, Guild> mutableMap = new HashMap<>(map);
                  mutableMap.remove(guild.getId());
                  channelWatcher
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

    return guildCreateListener.mergeWith(guildDeleteListener).replay(1);
  }

  private ConnectableObservable<ImmutableMap<Snowflake, Channel>> initChannelWatcher() {
    ImmutableMap<Snowflake, Channel> emptyChannelMap = ImmutableMap.of();

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
        eventObservable
            .ofType(ChannelCreateEvent.class)
            .map(ChannelCreateEvent::unwrap)
            .scan(
                emptyChannelMap,
                (map, channel) ->
                    ImmutableMap.<Snowflake, Channel>builder()
                        .put(channel.getId(), channel)
                        .build());
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

    return addableChannelCache
        .mergeWith(removeableChannelCache)
        .mergeWith(channelCreateListener)
        .mergeWith(channelDeleteListener)
        .replay(1);
  }

  @Override
  public Maybe<Guild> getGuild(@NonNull Snowflake id) {
    return guildWatcher
        .firstOrError()
        .flatMapMaybe(guildMap -> Maybes.fromNullable(guildMap.get(id)));
  }

  @Override
  public Maybe<Channel> getChannel(@NonNull Snowflake id) {
    return channelWatcher
        .firstOrError()
        .flatMapMaybe(channelMap -> Maybes.fromNullable(channelMap.get(id)));
  }
}
