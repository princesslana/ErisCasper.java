package com.github.princesslana.eriscasper.repository.event;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.event.GuildCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildDeleteEvent;
import com.github.princesslana.eriscasper.data.event.GuildEmojisUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildEmojisUpdateEventData;
import com.github.princesslana.eriscasper.data.event.GuildMemberAddEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberRemoveEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberRemoveEventData;
import com.github.princesslana.eriscasper.data.event.GuildMemberUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildMemberUpdateEventData;
import com.github.princesslana.eriscasper.data.event.GuildMembersChunkEvent;
import com.github.princesslana.eriscasper.data.event.GuildMembersChunkEventData;
import com.github.princesslana.eriscasper.data.event.GuildRoleCreateEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleCreateEventData;
import com.github.princesslana.eriscasper.data.event.GuildRoleDeleteEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleDeleteEventData;
import com.github.princesslana.eriscasper.data.event.GuildRoleUpdateEvent;
import com.github.princesslana.eriscasper.data.event.GuildRoleUpdateEventData;
import com.github.princesslana.eriscasper.data.event.GuildUpdateEvent;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.GuildMember;
import com.github.princesslana.eriscasper.data.resource.GuildMemberWithGuildId;
import com.github.princesslana.eriscasper.data.resource.ImmutableGuild;
import com.github.princesslana.eriscasper.data.resource.ImmutableGuildMember;
import com.github.princesslana.eriscasper.data.resource.Role;
import com.github.princesslana.eriscasper.data.resource.UnavailableGuild;
import com.github.princesslana.eriscasper.data.resource.User;
import com.github.princesslana.eriscasper.repository.FunctionData;
import com.github.princesslana.eriscasper.repository.GuildRepository;
import com.github.princesslana.eriscasper.rx.Maybes;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.observables.ConnectableObservable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GuildsFromEvents implements GuildRepository {

  private static final GuildFunctionData<Guild> ADD_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, guild) -> {
            map.put(guild.getId(), guild);
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<UnavailableGuild> REMOVE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, guild) -> {
            map.remove(guild.getId());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildEmojisUpdateEventData> EMOJIS_UPDATE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            map.replace(
                guild.getId(),
                ImmutableGuild.builder().from(guild).emojis(data.getEmojis()).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildMemberWithGuildId> MEMBER_ADD_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            map.replace(
                guild.getId(),
                ImmutableGuild.builder().from(guild).addMembers(data.getGuildMember()).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildMemberRemoveEventData> MEMBER_REMOVE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            List<GuildMember> members = new ArrayList<>(guild.getMembers());
            members
                .stream()
                .filter(member -> member.getUser().getId().equals(data.getUser().getId()))
                .findAny()
                .ifPresent(members::remove);
            map.replace(
                guild.getId(), ImmutableGuild.builder().from(guild).members(members).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildMembersChunkEventData> MEMBERS_CHUNK_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            Stack<GuildMember> toRemove = new Stack<>();
            List<GuildMember> members = new ArrayList<>(guild.getMembers());
            Set<Snowflake> container =
                data.getMembers()
                    .stream()
                    .map(GuildMember::getUser)
                    .map(User::getId)
                    .collect(Collectors.toSet());
            members
                .stream()
                .filter(member -> container.contains(member.getUser().getId()))
                .forEach(toRemove::push);
            toRemove.forEach(members::remove);
            members.addAll(data.getMembers());
            map.replace(
                guild.getId(), ImmutableGuild.builder().from(guild).members(members).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildMemberUpdateEventData> MEMBER_UPDATE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            List<GuildMember> members = new ArrayList<>(guild.getMembers());
            GuildMember initial =
                members
                    .stream()
                    .filter(member -> member.getUser().getId().equals(data.getUser().getId()))
                    .findFirst()
                    .orElse(null);
            if (initial == null) {
              throw new RuntimeException("Data not found where it should be.");
            }
            members.remove(initial);
            members.add(
                ImmutableGuildMember.builder()
                    .from(initial)
                    .nick(data.getNick())
                    .addAllRoles(data.getRoles())
                    .user(data.getUser())
                    .build());
            map.replace(
                guild.getId(), ImmutableGuild.builder().from(guild).members(members).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildRoleCreateEventData> ROLE_CREATE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            map.replace(
                guild.getId(),
                ImmutableGuild.builder().from(guild).addRoles(data.getRole()).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildRoleUpdateEventData> ROLE_UPDATE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            Snowflake id = data.getRole().getId();
            List<Role> roles = new ArrayList<>(guild.getRoles());
            Role initial =
                roles.stream().filter(role -> role.getId().equals(id)).findAny().orElse(null);
            if (initial == null) {
              throw new RuntimeException("Data not found where it should be.");
            }
            roles.remove(initial);
            roles.add(data.getRole());
            map.replace(guild.getId(), ImmutableGuild.builder().from(guild).roles(roles).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<GuildRoleDeleteEventData> ROLE_DELETE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            Guild guild = map.get(data.getGuildId());
            Snowflake id = data.getRoleId();
            List<Role> roles = new ArrayList<>(guild.getRoles());
            Role initial =
                roles.stream().filter(role -> role.getId().equals(id)).findAny().orElse(null);
            if (initial == null) {
              throw new RuntimeException("Data not found where it should be.");
            }
            roles.remove(initial);
            map.replace(guild.getId(), ImmutableGuild.builder().from(guild).roles(roles).build());
            return ImmutableMap.copyOf(map);
          });
  private static final GuildFunctionData<Guild> UPDATE_GUILD_FUNCTION =
      GuildFunctionData.of(
          (map, data) -> {
            map.replace(data.getId(), data);
            return ImmutableMap.copyOf(map);
          });

  private final ConnectableObservable<ImmutableMap<Snowflake, Guild>> guildWatcher;

  public GuildsFromEvents(Observable<Event> events) {
    // Merge all events which are meant to modify the guild by any means
    this.guildWatcher =
        events
            .ofType(GuildCreateEvent.class)
            .map(GuildCreateEvent::unwrap)
            .map(ADD_GUILD_FUNCTION)
            .mergeWith(
                events
                    .ofType(GuildDeleteEvent.class)
                    .map(GuildDeleteEvent::unwrap)
                    .map(REMOVE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildEmojisUpdateEvent.class)
                    .map(GuildEmojisUpdateEvent::unwrap)
                    .map(EMOJIS_UPDATE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildMemberAddEvent.class)
                    .map(GuildMemberAddEvent::unwrap)
                    .map(MEMBER_ADD_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildMemberRemoveEvent.class)
                    .map(GuildMemberRemoveEvent::unwrap)
                    .map(MEMBER_REMOVE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildMembersChunkEvent.class)
                    .map(GuildMembersChunkEvent::unwrap)
                    .map(MEMBERS_CHUNK_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildMemberUpdateEvent.class)
                    .map(GuildMemberUpdateEvent::unwrap)
                    .map(MEMBER_UPDATE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildRoleCreateEvent.class)
                    .map(GuildRoleCreateEvent::unwrap)
                    .map(ROLE_CREATE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildRoleUpdateEvent.class)
                    .map(GuildRoleUpdateEvent::unwrap)
                    .map(ROLE_UPDATE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildRoleDeleteEvent.class)
                    .map(GuildRoleDeleteEvent::unwrap)
                    .map(ROLE_DELETE_GUILD_FUNCTION))
            .mergeWith(
                events
                    .ofType(GuildUpdateEvent.class)
                    .map(GuildUpdateEvent::unwrap)
                    .map(UPDATE_GUILD_FUNCTION))
            .scan(ImmutableMap.<Snowflake, Guild>of(), (map, function) -> function.apply(map))
            .doOnError(Throwable::printStackTrace)
            .replay(1);
    this.guildWatcher.connect();
  }

  @Override
  public Maybe<Guild> getGuild(@NonNull Snowflake id) {
    return guildWatcher.firstElement().flatMap(map -> Maybes.fromNullable(map.get(id)));
  }

  @Override
  public Single<ImmutableMap<Snowflake, Guild>> getGuilds() {
    return guildWatcher.firstOrError();
  }

  private static class GuildFunctionData<X> extends FunctionData<Snowflake, Guild, X> {
    private GuildFunctionData(
        BiFunction<Map<Snowflake, Guild>, X, ImmutableMap<Snowflake, Guild>> function) {
      super(function);
    }

    private static <X> GuildFunctionData<X> of(
        BiFunction<Map<Snowflake, Guild>, X, ImmutableMap<Snowflake, Guild>> function) {
      return new GuildFunctionData<>(function);
    }
  }
}
