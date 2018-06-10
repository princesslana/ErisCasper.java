package com.github.princesslana.eriscasper.rest.guild;

import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#create-guild-role-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#create-guild-role-json-params</a>
 */
@Value.Immutable
public interface CreateGuildRoleRequest {
  Optional<String> getName();

  Optional<Integer> getPermissions();

  Optional<Integer> getColor();

  Optional<Boolean> isHoist();

  Optional<Boolean> isMentionable();

  static CreateGuildRoleRequest newRole() {
    return ImmutableCreateGuildRoleRequest.builder().build();
  }

  static CreateGuildRoleRequest of(String name) {
    return ImmutableCreateGuildRoleRequest.builder().name(name).build();
  }
}
