package com.github.princesslana.eriscasper.api.robot;

import static org.mockito.BDDMockito.then;

import com.github.javafaker.Faker;
import com.github.princesslana.eriscasper.BotContext;
import com.github.princesslana.eriscasper.data.ChannelId;
import com.github.princesslana.eriscasper.data.ImmutableMessage;
import com.github.princesslana.eriscasper.data.User;
import com.github.princesslana.eriscasper.data.Users;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.event.MessageCreate;
import com.github.princesslana.eriscasper.faker.DataFaker;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.Routes;
import com.github.princesslana.eriscasper.rest.SendMessageRequest;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

public class TestRobot {

  @Mock private Routes routes;

  private Robot subject;

  private BotContext bctx;
  private final PublishSubject<Event> events = PublishSubject.create();

  @BeforeMethod
  public void subject() {
    MockitoAnnotations.initMocks(this);
    subject = new Robot();

    bctx = new BotContext(events, routes, null);
  }

  public void hear_whenPing_shouldSendPong() {
    subject.hear("ping", ctx -> ctx.reply("pong"));
    TestObserver<Void> subscriber = run();

    ChannelId channelId = DataFaker.channelId();

    events.onNext(
        MessageCreate.of(
            ImmutableMessage.copyOf(DataFaker.message())
                .withContent("ping")
                .withChannelId(channelId)));

    thenShouldSend(channelId, "pong");
    subscriber.assertNotTerminated();
  }

  public void listen_whenForPing_shouldReplyPong() {
    subject.listen("ping", ctx -> ctx.reply("pong"));
    TestObserver<Void> subscriber = run();

    ChannelId channelId = DataFaker.channelId();
    User author = DataFaker.user();

    events.onNext(
        MessageCreate.of(
            ImmutableMessage.copyOf(DataFaker.message())
                .withAuthor(author)
                .withContent("+ping")
                .withChannelId(channelId)));

    String expectedResponse = Users.mentionByNickname(author) + " pong";

    thenShouldSend(channelId, expectedResponse);
    subscriber.assertNotTerminated();
  }

  public void listen_whenForPing_shouldNotHear() {
    subject.listen("ping", ctx -> ctx.reply("pong"));
    TestObserver<Void> subscriber = run();

    events.onNext(
        MessageCreate.of(ImmutableMessage.copyOf(DataFaker.message()).withContent("ping")));

    then(routes).shouldHaveZeroInteractions();
    subscriber.assertNotTerminated();
  }

  public void listen_whenForEchoRegex_shouldSendEcho() {
    subject.listen("echo (.+)", ctx -> ctx.send(ctx.match(1)));
    TestObserver<Void> subscriber = run();

    ChannelId channelId = DataFaker.channelId();

    String fact = Faker.instance().chuckNorris().fact();

    events.onNext(
        MessageCreate.of(
            ImmutableMessage.copyOf(DataFaker.message())
                .withContent("+echo " + fact)
                .withChannelId(channelId)));

    thenShouldSend(channelId, fact);
    subscriber.assertNotTerminated();
  }

  private TestObserver<Void> run() {
    return subject.apply(bctx).test();
  }

  private void thenShouldSend(ChannelId channelId, String msg) {
    then(routes)
        .should()
        .execute(RouteCatalog.createMessage(channelId), SendMessageRequest.ofText(msg));
  }
}
