package com.github.princesslana.eriscasper.rest.guild;

import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-role-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-guild-role-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildRoleRequest {
  String getName();

  Integer getPermissions();

  Integer getColor();

  Boolean isHoist();

  Boolean isMentionable();
}
