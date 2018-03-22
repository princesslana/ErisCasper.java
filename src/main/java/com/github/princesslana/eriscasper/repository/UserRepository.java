package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.User;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.event.Events;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class UserRepository {

  private final Flowable<Event<?>> events;

  public UserRepository(Flowable<Event<?>> events) {
    this.events = events;
  }

  public Single<User> getSelf() {
    return events
        .ofType(Events.Ready.class)
        .map(Event::getData)
        .map(d -> d.getUser())
        .firstOrError();
  }
}
