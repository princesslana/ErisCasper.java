package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Guild;
import java.util.Arrays;

public class GuildRoute {
  private final Snowflake id;

  private GuildRoute(Snowflake id) {
    this.id = id;
  }

  public Route<Void, Void> addGuildMemberRole(Snowflake userId, Snowflake guildId) {
    return Route.put(
        path("/members/%s/roles/%s", userId.unwrap(), guildId.unwrap()), Void.class, Void.class);
  }

  public Route<Void, Void> removeGuildMemberRole(Snowflake userId, Snowflake guildId) {
    return Route.delete(
        path("/members/%s/roles/%s", userId.unwrap(), guildId.unwrap()), Void.class);
  }

  private String path(String fmt, String... args) {
    return "/guilds/" + id.unwrap() + String.format(fmt, Arrays.asList(args).toArray());
  }

  public static GuildRoute on(Snowflake id) {
    return new GuildRoute(id);
  }

  public static GuildRoute on(Guild guild) {
    return on(guild.getId());
  }
}
