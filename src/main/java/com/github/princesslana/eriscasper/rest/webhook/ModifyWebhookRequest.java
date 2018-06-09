package com.github.princesslana.eriscasper.rest.webhook;

import com.github.princesslana.eriscasper.data.Snowflake;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/webhook#modify-webhook-json-params">
 *     https://discordapp.com/developers/docs/resources/webhook#modify-webhook-json-params</a>
 */
@Value.Immutable
public interface ModifyWebhookRequest {
  Optional<String> getName();

  Optional<String> getAvatar();

  Optional<Snowflake> getChannelId();
}
