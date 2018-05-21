package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.repository.GuildRepository;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;

public class GuildsFromEvents implements GuildRepository {

  private final Map<Snowflake, Guild> guildCache;

  public GuildsFromEvents(Observable<Event> eventObservable) {
    guildCache = new HashMap<>();
    eventObservable
        .ofType(GuildCreateEvent.class)
        .map(GuildCreateEvent::unwrap)
        .subscribe(guild -> guildCache.putIfAbsent(guild.getId(), guild));
  }

  @Override
  public Maybe<Guild> getGuild(Snowflake id) {
    Guild got = guildCache.get(id);
    return got == null ? Maybe.empty() : Maybe.just(got);
  }
}
