package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.request.CreateDmRequest;
import com.github.princesslana.eriscasper.data.request.CreateGroupDmRequest;
import com.github.princesslana.eriscasper.data.request.GetCurrentUserGuildsRequest;
import com.github.princesslana.eriscasper.data.request.ModifyCurrentUserRequest;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Connection;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.User;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;

public final class UserRoute {

  private UserRoute() {}

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#get-current-user">
   *     https://discordapp.com/developers/docs/resources/user#get-current-user</a>
   */
  public static Route<Void, User> getCurrentUser() {
    return Route.get(myPath(""), User.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#get-user">
   *     https://discordapp.com/developers/docs/resources/user#get-user</a>
   */
  public static Route<Void, User> getUser(Snowflake userId) {
    return Route.get(path("/%s", userId.unwrap()), User.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#modify-current-user">
   *     https://discordapp.com/developers/docs/resources/user#modify-current-user</a>
   */
  public static Route<ModifyCurrentUserRequest, User> modifyCurrentUser() {
    return Route.patch(myPath(""), User.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#get-current-user-guilds">
   *     https://discordapp.com/developers/docs/resources/user#get-current-user-guilds</acom.github.princesslana.eriscasper.data.request.GetCurrentUserGuildsRequest
   */
  public static Route<GetCurrentUserGuildsRequest, ImmutableList<Guild>> getCurrentUserGuilds() {
    return Route.get(myPath("/guilds"), Route.queryString(), Route.jsonArrayResponse(Guild.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#leave-guild">
   *     https://discordapp.com/developers/docs/resources/user#leave-guild</a>
   */
  public static Route<Void, Void> leaveGuild(Snowflake guildId) {
    return Route.delete(myPath("/guilds/%s", guildId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#get-user-dms">
   *     https://discordapp.com/developers/docs/resources/user#get-user-dms</a>
   */
  public static Route<Void, ImmutableList<Channel>> getUserDms() {
    return Route.get(myPath("/channels"), Route.jsonArrayResponse(Channel.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#create-dm">
   *     https://discordapp.com/developers/docs/resources/user#create-dm</a>
   */
  public static Route<CreateDmRequest, Channel> createDm() {
    return Route.post(myPath("/channels"), Channel.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#create-group-dm">
   *     https://discordapp.com/developers/docs/resources/user#create-group-dm</a>
   */
  public static Route<CreateGroupDmRequest, Channel> createGroupDm() {
    return Route.post(myPath("/channels"), Channel.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/user#get-user-connections">
   *     https://discordapp.com/developers/docs/resources/user#get-user-connections</a>
   */
  public static Route<Void, ImmutableList<Connection>> getUserConnections() {
    return Route.get(myPath("/connections"), Route.jsonArrayResponse(Connection.class));
  }

  private static String path(String fmt, String... args) {
    return "/users" + String.format(fmt, Arrays.asList(args).toArray());
  }

  private static String myPath(String fmt, String... args) {
    return path("/@me") + String.format(fmt, Arrays.asList(args).toArray());
  }
}
