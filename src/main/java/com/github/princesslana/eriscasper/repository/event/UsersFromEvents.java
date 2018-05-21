package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.ReadyEvent;
import com.github.princesslana.eriscasper.data.event.ReadyEventData;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.User;
import com.github.princesslana.eriscasper.repository.UserRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observables.ConnectableObservable;

import java.util.HashMap;
import java.util.Map;

public class UsersFromEvents implements UserRepository {

  private ConnectableObservable<User> self;
  private Map<Snowflake, Guild> guildCache;

  public UsersFromEvents(Observable<Event> events) {
    self = events.ofType(ReadyEvent.class).map(ReadyEvent::unwrap).map(ReadyEventData::getUser).replay(1);
    guildCache = new HashMap<>();
    events.ofType(GuildCreateEvent.class).map(GuildCreateEvent::unwrap).subscribe(guild -> guildCache.putIfAbsent(guild.getId(), guild));
    self.connect();
  }

  public Single<User> getSelf() {
    return self.firstOrError();
  }

  public Map<Snowflake, Guild> getGuildCache() {
    return guildCache;
  }

}
