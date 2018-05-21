package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.*;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.faker.DataFaker;
import io.reactivex.Maybe;
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
    events.onNext(GuildCreateEvent.of(guild));
    Channel channel = guild.getChannels().get(0);
    Maybe<Guild> possibleGuild = subject.getGuild(guild.getId());
    possibleGuild.test().assertValue(guild);
    subject.getChannel(channel.getId()).test().assertValue(channel);
    subject.getGuildFromChannel(channel.getId()).test().assertValue(guild);
    events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));
    Assert.assertNull(subject.getGuild(guild.getId()).blockingGet());
    Assert.assertNull(subject.getChannel(channel.getId()).blockingGet());
  }

  @Test
  public void getChannel_ShouldMaintainCacheOnCompletionAndDeletion() {
    Channel channel = DataFaker.channel();
    events.onNext(ChannelCreateEvent.of(channel));
    subject.getChannel(channel.getId()).test().assertValue(channel);
    Assert.assertNull(subject.getGuildFromChannel(channel.getId()).blockingGet());
    events.onNext(ChannelDeleteEvent.of(channel));
    Assert.assertNull(subject.getChannel(channel.getId()).blockingGet());
  }
}
