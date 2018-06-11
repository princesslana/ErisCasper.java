package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.event.GuildEmojisUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberAddEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberRemoveEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleDeleteEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildUpdateEvent;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildEmojisUpdateEventData;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildMemberRemoveEventData;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildMemberUpdateEventData;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildRoleCreateEventData;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildRoleDeleteEventData;
import com.github.princesslana.eriscasper.data.event.ImmutableGuildRoleUpdateEventData;
import com.github.princesslana.eriscasper.data.resource.Emoji;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.GuildMember;
import com.github.princesslana.eriscasper.data.resource.GuildMemberWithGuildId;
import com.github.princesslana.eriscasper.data.resource.ImmutableGuild;
import com.github.princesslana.eriscasper.data.resource.ImmutableGuildMember;
import com.github.princesslana.eriscasper.data.resource.ImmutableRole;
import com.github.princesslana.eriscasper.data.resource.Role;
import com.github.princesslana.eriscasper.faker.DataFaker;
import com.github.princesslana.eriscasper.rx.Maybes;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
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
    Guild guild = simpleCreateGuild();

    // Retrieve guild
    subject.getGuild(guild.getId()).subscribe(observer);

    // Assert state
    assertObserver(observer, guild);
  }

  @Test
  public void getGuild_whenDeleted_shouldRemoveCache() {
    TestObserver<Guild> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Delete guild
    events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild.getId())));

    // Retrieve guild
    subject.getGuild(guild.getId()).subscribe(observer);

    // Assert state
    assertObserver(observer);
  }

  @Test
  public void getGuild_whenDeleted_shouldMaintainOthers() {
    TestObserver<Guild> observer1 = new TestObserver<>();
    TestObserver<Guild> observer2 = new TestObserver<>();

    // Create guilds
    Guild guild1 = simpleCreateGuild();
    Guild guild2 = simpleCreateGuild();

    // Delete guild1
    events.onNext(GuildDeleteEvent.of(DataFaker.unavailableGuildFromGuild(guild1.getId())));

    // Retrieve guild1
    subject.getGuild(guild1.getId()).subscribe(observer1);

    // Assert state on guild1
    assertObserver(observer1);

    // Retrieve guild2
    subject.getGuild(guild2.getId()).subscribe(observer2);

    // Assert state on guild2
    assertObserver(observer2, guild2);
  }

  @Test
  public void getGuild_whenEmojisUpdate_shouldUpdateGuild() {
    TestObserver<Emoji> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Add guild emojis / update them
    Emoji emoji = DataFaker.emoji();
    events.onNext(
        GuildEmojisUpdateEvent.of(
            ImmutableGuildEmojisUpdateEventData.builder()
                .guildId(guild.getId())
                .addEmojis(emoji)
                .build()));

    // Retrieve guild emojis
    subject
        .getGuild(guild.getId())
        .map(Guild::getEmojis)
        .map(list -> list.get(0))
        .subscribe(observer);

    // Assert state
    assertObserver(observer, emoji);
  }

  @Test
  public void getGuild_whenMemberAdded_shouldAddToGuildMembers() {
    TestObserver<GuildMember> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Add guild member
    GuildMember member = simpleCreateGuildMember(guild.getId());

    // Retrieve member
    subject
        .getGuild(guild.getId())
        .map(Guild::getMembers)
        .map(list -> list.get(0))
        .subscribe(observer);

    // Assert state
    assertObserver(observer, member);
  }

  @Test
  public void getGuild_whenMemberRemoved_shouldRemoveFromGuildMembers() {
    TestObserver<GuildMember> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Add and remove guild member
    GuildMember member = simpleCreateGuildMember(guild.getId());
    events.onNext(
        GuildMemberRemoveEvent.of(
            ImmutableGuildMemberRemoveEventData.builder()
                .guildId(guild.getId())
                .user(member.getUser())
                .build()));

    // Retrieve `should be null` member
    subject
        .getGuild(guild.getId())
        .map(Guild::getMembers)
        .flatMap(list -> Maybes.fromOptional(list.stream().findFirst()))
        .subscribe(observer);

    // Assert state
    assertObserver(observer);
  }

  @Test
  public void getGuild_whenMemberUpdated_shouldUpdateInGuildMembers() {
    TestObserver<GuildMember> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Add and remove guild member
    GuildMember member = simpleCreateGuildMember(guild.getId());
    member = ImmutableGuildMember.builder().from(member).nick(DataFaker.username()).build();
    events.onNext(
        GuildMemberUpdateEvent.of(
            ImmutableGuildMemberUpdateEventData.builder()
                .guildId(guild.getId())
                .user(member.getUser())
                .nick(member.getNick().orElse(DataFaker.username()))
                .roles(member.getRoles())
                .build()));

    // Retrieve member
    subject
        .getGuild(guild.getId())
        .map(Guild::getMembers)
        .map(list -> list.get(0))
        .subscribe(observer);

    // Assert state
    assertObserver(observer, member);
  }

  @Test
  public void getGuild_whenRoleCreated_shouldCreateInGuildRoles() {
    TestObserver<Role> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Create role
    Role role = simpleCreateRole(guild.getId());

    // Retrieve role
    subject
        .getGuild(guild.getId())
        .map(Guild::getRoles)
        .map(list -> list.get(0))
        .subscribe(observer);

    // Assert state
    assertObserver(observer, role);
  }

  @Test
  public void getGuild_whenRoleUpdates_shouldUpdateInGuildRoles() {
    TestObserver<Role> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Create and update role
    Role role = simpleCreateRole(guild.getId());
    role = ImmutableRole.builder().from(role).name(DataFaker.username()).build();
    events.onNext(
        GuildRoleUpdateEvent.of(
            ImmutableGuildRoleUpdateEventData.builder().guildId(guild.getId()).role(role).build()));

    // Retrieve role
    subject
        .getGuild(guild.getId())
        .map(Guild::getRoles)
        .map(list -> list.get(0))
        .subscribe(observer);

    // Assert state
    assertObserver(observer, role);
  }

  @Test
  public void getGuild_whenRoleDeletes_shouldDeleteInGuildRoles() {
    TestObserver<Role> observer = new TestObserver<>();

    // Create guild
    Guild guild = simpleCreateGuild();

    // Create and delete role
    Role role = simpleCreateRole(guild.getId());
    events.onNext(
        GuildRoleDeleteEvent.of(
            ImmutableGuildRoleDeleteEventData.builder()
                .guildId(guild.getId())
                .roleId(role.getId())
                .build()));

    // Retrieve `should be null` role
    subject
        .getGuild(guild.getId())
        .map(Guild::getRoles)
        .flatMap(list -> Maybes.fromOptional(list.stream().findFirst()))
        .subscribe(observer);

    // Assert state
    assertObserver(observer);
  }

  @Test
  public void getGuild_whenGuildUpdates_shouldUpdateGuild() {
    TestObserver<Guild> observer = new TestObserver<>();

    // Create and update guild
    Guild guild = simpleCreateGuild();
    guild = ImmutableGuild.builder().from(DataFaker.guild()).id(guild.getId()).build();
    events.onNext(GuildUpdateEvent.of(guild));

    // Retrieve guild
    subject.getGuild(guild.getId()).subscribe(observer);

    // Assert state
    assertObserver(observer, guild);
  }

  @Ignore
  private Guild simpleCreateGuild() {
    Guild guild = DataFaker.guild();
    events.onNext(GuildCreateEvent.of(guild));
    return guild;
  }

  @Ignore
  private GuildMember simpleCreateGuildMember(Snowflake guildId) {
    GuildMember member = DataFaker.guildMember();
    events.onNext(GuildMemberAddEvent.of(new GuildMemberWithGuildId(guildId, member)));
    return member;
  }

  @Ignore
  private Role simpleCreateRole(Snowflake guildId) {
    Role role = DataFaker.role();
    events.onNext(
        GuildRoleCreateEvent.of(
            ImmutableGuildRoleCreateEventData.builder().guildId(guildId).role(role).build()));
    return role;
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
