package com.github.princesslana.eriscasper.rest.webhook;

import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/webhook#create-webhook-json-params">
 *     https://discordapp.com/developers/docs/resources/webhook#create-webhook-json-params</a>
 */
@Value.Immutable
public interface CreateWebhookRequest {
  String getName();

  String getAvatar();
}
