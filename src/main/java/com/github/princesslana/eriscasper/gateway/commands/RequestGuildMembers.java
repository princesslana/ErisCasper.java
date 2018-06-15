package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.gateway.ImmutablePayload;
import com.github.princesslana.eriscasper.gateway.OpCode;
import com.github.princesslana.eriscasper.gateway.Payload;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#request-guild-members">
 *     https://discordapp.com/developers/docs/topics/gateway#request-guild-members</a>
 */
@Value.Immutable
public interface RequestGuildMembers extends GatewayCommand {
  @JsonProperty("guild_id")
  Snowflake getGuildId();

  String getQuery();

  Integer getLimit();

  @Override
  default Payload toPayload(ObjectMapper jackson) {
    return ImmutablePayload.builder()
        .op(OpCode.REQUEST_GUILD_MEMBERS)
        .d(jackson.valueToTree(this))
        .build();
  }
}
