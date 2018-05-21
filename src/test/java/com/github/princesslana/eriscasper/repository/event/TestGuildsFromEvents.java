package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.faker.DataFaker;
import io.reactivex.Maybe;
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
	public void getGuild_whenAfterGuildEvent_shouldCacheGuild() {
		Guild guild = DataFaker.guild();
		events.onNext(GuildCreateEvent.of(guild));
		Maybe<Guild> possibleGuild = subject.getGuild(guild.getId());
		possibleGuild.test().assertValue(guild);
	}
}
