package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.ReadyEvent;
import com.github.princesslana.eriscasper.data.resource.UserResource;
import com.github.princesslana.eriscasper.repository.UserRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observables.ConnectableObservable;

public class UsersFromEvents implements UserRepository {

  private ConnectableObservable<UserResource> self;

  public UsersFromEvents(Observable<Event> events) {
    self = events.ofType(ReadyEvent.class).map(ReadyEvent::unwrap).map(d -> d.getUser()).replay(1);

    self.connect();
  }

  public Single<UserResource> getSelf() {
    return self.firstOrError();
  }
}
