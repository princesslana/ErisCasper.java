package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-channel-positions-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-guild-channel-positions-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildChannelPositionsRequest {
  Snowflake getId();

  Integer getPosition();
}
