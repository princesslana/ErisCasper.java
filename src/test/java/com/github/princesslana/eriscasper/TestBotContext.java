package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.MessageCreateEvent;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.faker.DataFaker;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class TestBotContext {

  @Test
  public void on_whenMessageCreateEvent_applysWithMessage() {
    Message message = DataFaker.message();

    PublishSubject<Event> publisher = PublishSubject.create();

    BotContext bctx = new BotContext(publisher, null, null, null);

    List<Message> received = new ArrayList<>();

    bctx.on(MessageCreateEvent.class, m -> Completable.fromAction(() -> received.add(m))).test();

    publisher.onNext(MessageCreateEvent.of(message));

    Assertions.assertThat(received).hasSize(1).containsOnly(message);
  }
}
