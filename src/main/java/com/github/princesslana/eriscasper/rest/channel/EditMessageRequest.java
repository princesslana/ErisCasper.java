package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.resource.Embed;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/channel#edit-message-json-params">
 *     https://discordapp.com/developers/docs/resources/channel#edit-message-json-params</a>
 */
@Value.Immutable
public interface EditMessageRequest {
  Optional<String> getContent();

  Optional<Embed> getEmbed();
}
