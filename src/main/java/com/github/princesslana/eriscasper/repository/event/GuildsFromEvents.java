package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.*;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.repository.GuildRepository;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GuildsFromEvents implements GuildRepository {

  private final Map<Snowflake, Guild> guildCache;
  private final Map<Snowflake, Channel> channelCache;

  public GuildsFromEvents(Observable<Event> eventObservable) {
    guildCache = new HashMap<>();
    channelCache = new HashMap<>();
    eventObservable
        .ofType(GuildCreateEvent.class)
        .map(GuildCreateEvent::unwrap)
        .subscribe(
            guild -> {
              guildCache.putIfAbsent(guild.getId(), guild);
              guild
                  .getChannels()
                  .forEach(channel -> channelCache.putIfAbsent(channel.getId(), channel));
            });
    eventObservable
        .ofType(GuildDeleteEvent.class)
        .map(GuildDeleteEvent::unwrap)
        .subscribe(
            guild -> {
              Snowflake id = guild.getId();
              guildCache.remove(id);
              Stack<Snowflake> removingChannelStack = new Stack<>();
              channelCache
                  .entrySet()
                  .stream()
                  .filter(entry -> id.equals(entry.getValue().getGuildId().orElse(null)))
                  .forEach(entry -> removingChannelStack.push(entry.getKey()));
              while (!removingChannelStack.isEmpty()) {
                channelCache.remove(removingChannelStack.pop());
              }
            });
    eventObservable
        .ofType(ChannelCreateEvent.class)
        .map(ChannelCreateEvent::unwrap)
        .subscribe(channel -> channelCache.putIfAbsent(channel.getId(), channel));
    eventObservable
        .ofType(ChannelDeleteEvent.class)
        .map(ChannelDeleteEvent::unwrap)
        .subscribe(channel -> channelCache.remove(channel.getId()));
  }

  @Override
  public Maybe<Guild> getGuild(Snowflake id) {
    if (id == null) return Maybe.empty();
    Guild got = guildCache.get(id);
    return got == null ? Maybe.empty() : Maybe.just(got);
  }

  @Override
  public Maybe<Channel> getChannel(Snowflake id) {
    if (id == null) return Maybe.empty();
    Channel got = channelCache.get(id);
    return got == null ? Maybe.empty() : Maybe.just(got);
  }

  @Override
  public Maybe<Guild> getGuildFromChannel(Snowflake channelId) {
    if (channelId == null) return Maybe.empty();
    Channel channel = getChannel(channelId).blockingGet();
    return channel == null ? Maybe.empty() : getGuild(channel.getGuildId().orElse(null));
  }
}
