package com.github.princesslana.eriscasper.rest.webhook;

import com.github.princesslana.eriscasper.data.resource.Embed;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/webhook#execute-webhook-jsonform-params">
 *     https://discordapp.com/developers/docs/resources/webhook#execute-webhook-jsonform-params</a>
 */
@Value.Immutable
public interface ExecuteWebhookRequest { // TODO unfinished see WebhookRoute
  Optional<String> getContent();

  Optional<String> getUsername();

  Optional<String> getAvatarUrl();

  Optional<Boolean> isTts();

  Optional<File> getFile();

  ImmutableList<Embed> getEmbeds();
}
