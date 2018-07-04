package com.github.princesslana.eriscasper.rest.guild;

import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#modify-guild-integration-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-guild-integration-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildIntegrationRequest {
  Integer getExpireBehavior();

  Integer getExpireGracePeriod();

  Boolean getEnableEmoticons();
}
