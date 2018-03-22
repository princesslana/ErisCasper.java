package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.User;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.event.Events;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {

  private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

  private User self;

  public UserRepository() {}

  public User getSelf() {
    return self;
  }

  public Completable connect(Flowable<Event<?>> events) {
    return events
        .ofType(Events.Ready.class)
        .map(Event::getData)
        .map(d -> d.getUser())
        .doOnNext(u -> LOG.debug("Updating self user"))
        .doOnNext(u -> self = u)
        .ignoreElements();
  }
}
