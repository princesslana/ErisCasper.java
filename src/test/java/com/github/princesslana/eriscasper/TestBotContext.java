package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.MessageCreateEvent;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.faker.DataFaker;
import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.rest.Routes;
import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestBotContext {

  @Mock private Routes routes;

  @Mock private Gateway gateway;

  @BeforeMethod
  public void mockito() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void on_whenMessageCreateEvent_applysWithMessage() {
    Message message = DataFaker.message();

    PublishSubject<Event> publisher = PublishSubject.create();

    BotContext bctx = new BotContext(publisher, routes, gateway, null);

    List<Message> received = new ArrayList<>();

    bctx.on(MessageCreateEvent.class, m -> Completable.fromAction(() -> received.add(m))).test();

    publisher.onNext(MessageCreateEvent.of(message));

    Assertions.assertThat(received).hasSize(1).containsOnly(message);
  }
}
