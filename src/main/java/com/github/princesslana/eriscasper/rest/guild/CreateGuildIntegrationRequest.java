package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#create-guild-integration-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#create-guild-integration-json-params</a>
 */
@Value.Immutable
public interface CreateGuildIntegrationRequest {
  String getType();

  Snowflake getId();
}
