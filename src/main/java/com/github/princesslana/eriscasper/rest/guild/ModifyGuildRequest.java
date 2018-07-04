package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/guild#modify-guild-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-guild-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildRequest {
  Optional<String> getName();

  Optional<String> getRegion();

  Optional<Integer> getVerificationLevel();

  Optional<Integer> getDefaultMessageNotifications();

  Optional<Integer> getExplicitContentFilter();

  Optional<Snowflake> getAfkChannelId();

  Optional<Integer> getAfkTimeout();

  Optional<String> getIcon();

  Optional<Snowflake> getOwnerId();

  Optional<String> getSplash();

  Optional<Snowflake> getSystemChannelId();
}
