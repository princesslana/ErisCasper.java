package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Ban;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.data.resource.GuildEmbed;
import com.github.princesslana.eriscasper.data.resource.GuildMember;
import com.github.princesslana.eriscasper.data.resource.Integration;
import com.github.princesslana.eriscasper.data.resource.InviteWithMetadata;
import com.github.princesslana.eriscasper.data.resource.PartialInvite;
import com.github.princesslana.eriscasper.data.resource.Role;
import com.github.princesslana.eriscasper.data.resource.VoiceRegion;
import com.github.princesslana.eriscasper.rest.guild.AddGuildMemberRequest;
import com.github.princesslana.eriscasper.rest.guild.BeginGuildPruneRequest;
import com.github.princesslana.eriscasper.rest.guild.CreateGuildBanRequest;
import com.github.princesslana.eriscasper.rest.guild.CreateGuildIntegrationRequest;
import com.github.princesslana.eriscasper.rest.guild.CreateGuildRoleRequest;
import com.github.princesslana.eriscasper.rest.guild.GetGuildPruneCountRequest;
import com.github.princesslana.eriscasper.rest.guild.GuildChannelCreateRequest;
import com.github.princesslana.eriscasper.rest.guild.GuildCreateRequest;
import com.github.princesslana.eriscasper.rest.guild.ListGuildMemberRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyCurrentNickRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildChannelPositionsRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildIntegrationRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildMemberRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildRolePositionsRequest;
import com.github.princesslana.eriscasper.rest.guild.ModifyGuildRoleRequest;
import com.github.princesslana.eriscasper.util.Pruned;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Optional;

public class GuildRoute {
  private final Snowflake id;

  private GuildRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild</a>
   */
  public Route<Void, Guild> getGuild() {
    return Route.get(path(""), Guild.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild</a>
   */
  public Route<ModifyGuildRequest, Guild> modifyGuild() {
    return Route.patch(path(""), ModifyGuildRequest.class, Guild.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#delete-guild">
   *     https://discordapp.com/developers/docs/resources/guild#delete-guild</a>
   */
  public Route<Void, Void> deleteGuild() {
    return Route.delete(path(""), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-channels">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-channels</a>
   */
  public Route<Void, ImmutableList<Channel>> getGuildChannels() {
    return Route.get(path("/channels"), Route.jsonArrayResponse(Channel.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild-channel">
   *     https://discordapp.com/developers/docs/resources/guild#create-guild-channel</a>
   */
  public Route<GuildChannelCreateRequest, Channel> createGuildChannel() {
    return Route.post(path("/channels"), GuildChannelCreateRequest.class, Channel.class);
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-channel-positions">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-channel-positions</a>
   */
  public Route<ModifyGuildChannelPositionsRequest, Void> modifyGuildChannelPositions() {
    return Route.patch(path("/channels"), ModifyGuildChannelPositionsRequest.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-member">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-member</a>
   */
  public Route<Void, GuildMember> getGuildMember(Snowflake userId) {
    return Route.get(path("/members/%s", userId.unwrap()), GuildMember.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#list-guild-members">
   *     https://discordapp.com/developers/docs/resources/guild#list-guild-members</a>
   */
  public Route<ListGuildMemberRequest, ImmutableList<GuildMember>> listGuildMembers() {
    return Route.get(
        path("/members"),
        Route.queryString(ListGuildMemberRequest::toQueryString),
        Route.jsonArrayResponse(GuildMember.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#add-guild-member">
   *     https://discordapp.com/developers/docs/resources/guild#add-guild-member</a>
   */
  public Route<AddGuildMemberRequest, Optional<GuildMember>> addGuildMember(Snowflake userId) {
    return Route.get(
        path("/members/%s", userId.unwrap()),
        Route.jsonRequestBody(),
        (response) ->
            response.code() == 204
                ? Optional.empty()
                : Optional.of(Data.fromJson(response.body().string(), GuildMember.class)));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild-member">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-member</a>
   */
  public Route<ModifyGuildMemberRequest, Void> modifyGuildMember(Snowflake userId) {
    return Route.patch(
        path("/members/%s", userId.unwrap()), ModifyGuildMemberRequest.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-current-user-nick">
   *     https://discordapp.com/developers/docs/resources/guild#modify-current-user-nick</a>
   */
  public Route<ModifyCurrentNickRequest, String> modifyCurrentUserNick() {
    return Route.patch(path("/members/@me/nick"), ModifyCurrentNickRequest.class, String.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#add-guild-member-role">
   *     https://discordapp.com/developers/docs/resources/guild#add-guild-member-role</a>
   */
  public Route<Void, Void> addGuildMemberRole(Snowflake userId, Snowflake roleId) {
    return Route.put(
        path("/members/%s/roles/%s", userId.unwrap(), roleId.unwrap()), Void.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#remove-guild-member-role">
   *     https://discordapp.com/developers/docs/resources/guild#remove-guild-member-role</a>
   */
  public Route<Void, Void> removeGuildMemberRole(Snowflake userId, Snowflake roleId) {
    return Route.delete(path("/members/%s/roles/%s", userId.unwrap(), roleId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#remove-guild-member">
   *     https://discordapp.com/developers/docs/resources/guild#remove-guild-member</a>
   */
  public Route<Void, Void> removeGuildMember(Snowflake userId) {
    return Route.delete(path("/members/%s", userId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-bans">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-bans</a>
   */
  public Route<Void, ImmutableList<Ban>> getGuildBans() {
    return Route.get(path("/bans"), Route.jsonArrayResponse(Ban.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-ban">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-ban</a>
   */
  public Route<Void, Ban> getGuildBan(Snowflake userId) {
    return Route.get(path("/bans/%s" + userId.unwrap()), Ban.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild-ban">
   *     https://discordapp.com/developers/docs/resources/guild#create-guild-ban</a>
   */
  public Route<CreateGuildBanRequest, Void> createGuildBan(Snowflake userId) {
    return Route.put(
        path("/bans/%s", userId.unwrap()),
        Route.queryString(CreateGuildBanRequest::toQueryString),
        Route.noResponse());
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#remove-guild-ban">
   *     https://discordapp.com/developers/docs/resources/guild#remove-guild-ban</a>
   */
  public Route<Void, Void> removeGuildBan(Snowflake userId) {
    return Route.delete(path("/bans/%s", userId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-roles">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-roles</a>
   */
  public Route<Void, ImmutableList<Role>> getGuildRoles() {
    return Route.get(path("/roles"), Route.jsonArrayResponse(Role.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild-role">
   *     https://discordapp.com/developers/docs/resources/guild#create-guild-role</a>
   */
  public Route<CreateGuildRoleRequest, Role> createGuildRole() {
    return Route.post(path("/roles"), CreateGuildRoleRequest.class, Role.class);
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-role-positions">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-role-positions</a>
   */
  public Route<ModifyGuildRolePositionsRequest, ImmutableList<Role>> modifyGuildRolePositions() {
    return Route.patch(
        path("/roles"), Route.jsonRequestBody(), Route.jsonArrayResponse(Role.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild-role">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-role</a>
   */
  public Route<ModifyGuildRoleRequest, Role> modifyRole(Snowflake roleId) {
    return Route.patch(
        path("/roles/%s", roleId.unwrap()), ModifyGuildRoleRequest.class, Role.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#delete-guild-role">
   *     https://discordapp.com/developers/docs/resources/guild#delete-guild-role</a>
   */
  public Route<Void, Void> deleteRole(Snowflake roleId) {
    return Route.delete(path("/roles/%s", roleId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-prune-count">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-prune-count</a>
   */
  public Route<GetGuildPruneCountRequest, Pruned> getGuildPruneCount() {
    return Route.get(
        path("/prune"),
        Route.queryString(GetGuildPruneCountRequest::toQueryString),
        Route.jsonResponse(Pruned.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#begin-guild-prune">
   *     https://discordapp.com/developers/docs/resources/guild#begin-guild-prune</a>
   */
  public Route<BeginGuildPruneRequest, Pruned> beginGuildPrune() {
    return Route.post(
        path("/prune"),
        Route.queryString(BeginGuildPruneRequest::toQueryString),
        Route.jsonResponse(Pruned.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-voice-regions">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-voice-regions</a>
   */
  public Route<Void, ImmutableList<VoiceRegion>> getGuildVoiceRegions() {
    return Route.get(path("/regions"), Route.jsonArrayResponse(VoiceRegion.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-invites">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-invites</a>
   */
  public Route<Void, ImmutableList<InviteWithMetadata>> getGuildInvites() {
    return Route.get(path("/invites"), Route.jsonArrayResponse(InviteWithMetadata.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-integrations">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-integrations</a>
   */
  public Route<Void, ImmutableList<Integration>> getGuildIntegrations() {
    return Route.get(path("/integrations"), Route.jsonArrayResponse(Integration.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild-integration">
   *     https://discordapp.com/developers/docs/resources/guild#create-guild-integration</a>
   */
  public Route<CreateGuildIntegrationRequest, Void> createGuildIntegration() {
    return Route.post(path("/integrations"), CreateGuildIntegrationRequest.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild-integration">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-integration</a>
   */
  public Route<ModifyGuildIntegrationRequest, Void> modifyGuildIntegration(
      Snowflake integrationId) {
    return Route.patch(
        path("/integrations/%s", integrationId.unwrap()),
        ModifyGuildIntegrationRequest.class,
        Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#delete-guild-integration">
   *     https://discordapp.com/developers/docs/resources/guild#delete-guild-integration</a>
   */
  public Route<Void, Void> deleteGuildIntegration(Snowflake integrationId) {
    return Route.delete(path("/integrations/%s", integrationId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#sync-guild-integration">
   *     https://discordapp.com/developers/docs/resources/guild#sync-guild-integration</a>
   */
  public Route<Void, Void> syncGuildIntegration(Snowflake integrationId) {
    return Route.post(
        path("/integrations/%s/sync", integrationId.unwrap()), Void.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-embed">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-embed</a>
   */
  public Route<Void, GuildEmbed> getGuildEmbed() {
    return Route.get(path("/embed"), GuildEmbed.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild-embed">
   *     https://discordapp.com/developers/docs/resources/guild#modify-guild-embed</a>
   */
  public Route<GuildEmbed, GuildEmbed> modifyGuildEmbed() {
    return Route.patch(path("/embed"), GuildEmbed.class, GuildEmbed.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#get-guild-vanity-url">
   *     https://discordapp.com/developers/docs/resources/guild#get-guild-vanity-url</a>
   */
  public Route<Void, PartialInvite> getGuildVanityUrl() {
    return Route.get(path("/vanity-url"), PartialInvite.class);
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

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild">
   *     https://discordapp.com/developers/docs/resources/guild#create-guild</a>
   */
  public static Route<GuildCreateRequest, Guild> createGuild() {
    return Route.post("/guilds", GuildCreateRequest.class, Guild.class);
  }
}
