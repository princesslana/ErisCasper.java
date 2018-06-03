package com.github.princesslana.eriscasper.rest.webhook;

import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/webhook#execute-githubcompatible-webhook-querystring-params">
 *     https://discordapp.com/developers/docs/resources/webhook#execute-githubcompatible-webhook-querystring-params</a>
 */
@Value.Immutable
public interface ExecuteGithubWebhookRequest {
  Optional<Boolean> isWait();

  default String toQueryString() {
    return new QueryStringBuilder().addBool("wait", isWait()).build();
  }
}
