package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.princesslana.eriscasper.data.Snowflake;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#request-guild-members">
 *     https://discordapp.com/developers/docs/topics/gateway#request-guild-members</a>
 */
@Value.Immutable
public interface RequestGuildMembers {
  @JsonProperty("guild_id")
  Snowflake getGuildId();

  @JsonProperty("query")
  String getQuery();

  @JsonProperty("limit")
  Integer getLimit();
}
