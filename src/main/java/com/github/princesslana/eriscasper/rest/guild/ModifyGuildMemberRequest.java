package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Role;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-member-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-guild-member-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildMemberRequest {
  Optional<String> getNick();

  ImmutableList<Role> getRoles();

  Optional<Boolean> isMute();

  Optional<Boolean> isDeaf();

  Optional<Snowflake> getChannelId();
}
