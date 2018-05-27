package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.ChannelCreateEvent;
import com.github.princesslana.eriscasper.data.event.ChannelDeleteEvent;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.faker.DataFaker;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
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
	public void getGuild_whenCreated_shouldCache() {
		TestObserver<Guild> observer = new TestObserver<>();

		// Create guild
		Guild guild = DataFaker.guild();
		events.onNext(GuildCreateEvent.of(guild));

		// Retrieve guild
		subject.getGuild(guild.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertValue(guild);
	}

	@Test
	public void getGuild_whenDeleted_shouldRemoveCache() {
		TestObserver<Guild> observer = new TestObserver<>();

		// Create guild
		Guild guild = DataFaker.guild();
		events.onNext(GuildCreateEvent.of(guild));

		// Delete guild
		events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));

		// Retrieve guild
		subject.getGuild(guild.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertNoValues();
	}

	@Test
	public void getGuild_whenDeleted_shouldMaintainOthers() {
		TestObserver<Guild> observer1 = new TestObserver<>();
		TestObserver<Guild> observer2 = new TestObserver<>();

		// Create guilds
		Guild guild1 = DataFaker.guild();
		Guild guild2 = DataFaker.guild();
		events.onNext(GuildCreateEvent.of(guild1));
		events.onNext(GuildCreateEvent.of(guild2));

		// Delete guild1
		events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild1.getId())));

		// Retrieve guild1
		subject.getGuild(guild1.getId()).subscribe(observer1);

		// Assert state of guild1
		observer1.assertComplete();
		observer1.assertNoValues();

		// Retrieve guild2
		subject.getGuild(guild2.getId()).subscribe(observer2);

		// Assert state of guild2
		observer2.assertComplete();
		observer2.assertValue(guild2);
	}

	@Test
	public void getChannel_whenGuildCreated_shouldCacheChannel() {
		TestObserver<Channel> observer = new TestObserver<>();

		// Create guild, define channel
		Guild guild = DataFaker.guild();
		Channel channel = guild.getChannels().get(0);
		events.onNext(GuildCreateEvent.of(guild));

		// Retrieve channel
		subject.getChannel(channel.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertValue((ch) -> ch.getId().equals(channel.getId()));
		observer.assertValue((ch) -> guild.getId().equals(ch.getGuildId().orElse(null)));
	}

	@Test
	public void getChannel_whenGuildDeleted_shouldRemoveCachedChannel() {
		TestObserver<Channel> observer = new TestObserver<>();

		// Create guild, define channel
		Guild guild = DataFaker.guild();
		Channel channel = guild.getChannels().get(0);
		events.onNext(GuildCreateEvent.of(guild));

		// Delete guild
		events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));

		// Retrieve channel
		subject.getChannel(channel.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertNoValues();
	}

	@Test
	public void getChannel_whenCreated_shouldCache() {
		TestObserver<Channel> observer = new TestObserver<>();

		// Create channel
		Channel channel = DataFaker.channel();
		events.onNext(ChannelCreateEvent.of(channel));

		// Retrieve channel
		subject.getChannel(channel.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertValue(channel);
	}

	@Test
	public void getChannel_whenDeleted_shouldRemoveCache() {
		TestObserver<Channel> observer = new TestObserver<>();

		// Create channel
		Channel channel = DataFaker.channel();
		events.onNext(ChannelCreateEvent.of(channel));

		// Delete channel
		events.onNext(ChannelDeleteEvent.of(channel));

		// Retrieve channel
		subject.getChannel(channel.getId()).subscribe(observer);

		// Assert state
		observer.assertComplete();
		observer.assertNoValues();
	}

	@Test
	public void getChannel_whenDeleted_shouldMaintainOthers() {
		TestObserver<Channel> observer1 = new TestObserver<>();
		TestObserver<Channel> observer2 = new TestObserver<>();

		// Create guilds
		Channel channel1 = DataFaker.channel();
		Channel channel2 = DataFaker.channel();
		events.onNext(ChannelCreateEvent.of(channel1));
		events.onNext(ChannelCreateEvent.of(channel2));

		// Delete channel1
		events.onNext(ChannelDeleteEvent.of(channel1));

		// Retrieve channel1
		subject.getChannel(channel1.getId()).subscribe(observer1);

		// Assert state of channel1
		observer1.assertComplete();
		observer1.assertNoValues();

		// Retrieve channel2
		subject.getChannel(channel2.getId()).subscribe(observer2);

		// Assert state of channel2
		observer2.assertComplete();
		observer2.assertValue(channel2);
	}
}
