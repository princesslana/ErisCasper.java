package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.ChannelPinsUpdateEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.event.ImmutableChannelPinsUpdateEventData;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.faker.DataFaker;
import com.google.common.collect.ImmutableList;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class TestChannelsFromEvents {

  private PublishSubject<Event> events = PublishSubject.create();

  private ChannelsFromEvents subject;

  @BeforeMethod
  public void subject() {
    subject = new ChannelsFromEvents(events);
  }

  @Test
  public void getChannel_whenGuildCreated_shouldCacheChannel() {
    TestObserver<Channel> observer = new TestObserver<>();

    Guild guild = simpleCreateGuild();
    Channel channel = guild.getChannels().orElse(ImmutableList.of()).get(0);

    subject.getChannel(channel.getId()).subscribe(observer);

    observer.assertComplete();
    observer.assertValue((ch) -> ch.getId().equals(channel.getId()));
    observer.assertValue((ch) -> guild.getId().equals(ch.getGuildId().orElse(null)));
  }

  @Test
  public void getChannel_whenGuildDeleted_shouldRemoveCachedChannel() {
    TestObserver<Channel> observer = new TestObserver<>();

    Guild guild = simpleCreateGuild();
    Channel channel = guild.getChannels().orElse(ImmutableList.of()).get(0);

    events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));

    subject.getChannel(channel.getId()).subscribe(observer);

    assertObserver(observer);
  }

  @Test
  public void getChannel_whenCreated_shouldCache() {
    TestObserver<Channel> observer = new TestObserver<>();

    Channel channel = simpleCreateChannel();

    subject.getChannel(channel.getId()).subscribe(observer);

    assertObserver(observer, channel);
  }

  @Test
  public void getChannel_whenDeleted_shouldRemoveCache() {
    TestObserver<Channel> observer = new TestObserver<>();

    Channel channel = simpleCreateChannel();

    events.onNext(ChannelDeleteEvent.of(channel));

    subject.getChannel(channel.getId()).subscribe(observer);

    assertObserver(observer);
  }

  @Test
  public void getChannel_whenDeleted_shouldMaintainOthers() {
    TestObserver<Channel> observer1 = new TestObserver<>();
    TestObserver<Channel> observer2 = new TestObserver<>();

    Channel channel1 = simpleCreateChannel();
    Channel channel2 = simpleCreateChannel();

    events.onNext(ChannelDeleteEvent.of(channel1));

    subject.getChannel(channel1.getId()).subscribe(observer1);

    assertObserver(observer1);

    subject.getChannel(channel2.getId()).subscribe(observer2);

    assertObserver(observer2, channel2);
  }

  @Test
  public void getChannel_whenPinsUpdate_shouldUpdateChannel() {
    TestObserver<OffsetDateTime> observer = new TestObserver<>();

    Channel channel = simpleCreateChannel();

    OffsetDateTime timestamp = OffsetDateTime.now();
    events.onNext(
        ChannelPinsUpdateEvent.of(
            ImmutableChannelPinsUpdateEventData.builder()
                .channelId(channel.getId())
                .lastPinTimestamp(timestamp)
                .build()));

    subject
        .getChannel(channel.getId())
        .map(Channel::getLastPinTimestamp)
        .map(Optional::get)
        .subscribe(observer);

    assertObserver(observer, timestamp);
  }

  @Ignore
  private Channel simpleCreateChannel() {
    Channel channel = DataFaker.channel();
    events.onNext(ChannelCreateEvent.of(channel));
    return channel;
  }

  @Ignore
  private Guild simpleCreateGuild() {
    Guild guild = DataFaker.guild();
    events.onNext(GuildCreateEvent.of(guild));
    return guild;
  }

  @Ignore
  private <T> void assertObserver(TestObserver<T> observer) {
    observer.assertNoErrors();
    observer.assertComplete();
    observer.assertNoValues();
  }

  @Ignore
  private <T> void assertObserver(TestObserver<T> observer, T value) {
    observer.assertNoErrors();
    observer.assertComplete();
    observer.assertValue(value);
  }
}
