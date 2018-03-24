package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.ImmutableReadyData;
import com.github.princesslana.eriscasper.data.ReadyData;
import com.github.princesslana.eriscasper.data.User;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.event.ReadyTuple;
import com.github.princesslana.eriscasper.faker.DiscordFaker;
import com.github.princesslana.eriscasper.faker.UserFaker;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.testng.annotations.BeforeMethod;

public class TestUsersFromEvents {

  private PublishSubject<Event> events;

  private UsersFromEvents subject;

  @BeforeMethod
  public void subject() {
    subject = new UsersFromEvents(events);
  }

  public void getSelf_whenAfterReady_shouldReturnSelf() {
    TestObserver<User> observer = new TestObserver<>();

    User self = UserFaker.user();
    ReadyData ready =
        ImmutableReadyData.builder().user(self).sessionId(DiscordFaker.sessionId()).build();
    events.onNext(ReadyTuple.of(ready));

    subject.getSelf().subscribe(observer);

    observer.assertValuesOnly(self);
  }
}
