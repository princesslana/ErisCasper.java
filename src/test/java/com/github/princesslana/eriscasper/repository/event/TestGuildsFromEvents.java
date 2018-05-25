package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.faker.DataFaker;
import io.reactivex.subjects.PublishSubject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestGuildsFromEvents {

  private PublishSubject<Event> events = PublishSubject.create();

  private GuildsFromEvents subject;

  @BeforeMethod
  public void subject() {
    subject = new GuildsFromEvents(events);
  }

  @Test
  public void getGuild_whenAfterGuildEvent_shouldCacheGuild() {
    Guild guild = DataFaker.guild();
    Channel channel = guild.getChannels().get(0);
    events.onNext(GuildCreateEvent.of(guild));
    Guild n = DataFaker.guild();
    events.onNext(GuildCreateEvent.of(n));
    subject.getGuild(n.getId()).test().assertValue(n);
    // Called multiple times to assure the guild is properly cached in the stream instead of
    // disposed.
    subject.getGuild(guild.getId()).test().assertValue(guild);
    subject.getGuild(guild.getId()).test().assertValue(guild);
    events.onNext(GuildCreateEvent.of(DataFaker.guild()));
    subject.getChannel(channel.getId()).map(Channel::getId).test().assertValue(channel.getId());
    events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));
    Assert.assertNull(subject.getGuild(guild.getId()).blockingGet());
    Assert.assertNull(subject.getChannel(channel.getId()).blockingGet());
    subject.getGuild(n.getId()).test().assertValue(n);
  }

  @Test
  public void getChannel_ShouldMaintainCacheOnCompletionAndDeletion() {
    Channel channel = DataFaker.channel();
    Channel n = DataFaker.channel();
    events.onNext(ChannelCreateEvent.of(channel));
    // Called multiple times to assure the channel is properly cached in the stream instead of
    // disposed
    subject.getChannel(channel.getId()).test().assertValue(channel);
    subject.getChannel(channel.getId()).test().assertValue(channel);
    events.onNext(ChannelCreateEvent.of(n));
    subject.getChannel(n.getId()).test().assertValue(n);
    events.onNext(ChannelDeleteEvent.of(channel));
    Assert.assertNull(subject.getChannel(channel.getId()).blockingGet());
    subject.getChannel(n.getId()).test().assertValue(n);
  }
}
