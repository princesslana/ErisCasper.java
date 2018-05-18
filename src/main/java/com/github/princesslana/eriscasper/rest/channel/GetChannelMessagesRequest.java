package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.Snowflake;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/channel#get-channel-messages-query-string-params">
 *     https://discordapp.com/developers/docs/resources/channel#get-channel-messages-query-string-params</a>
 */
@Value.Immutable
public interface GetChannelMessagesRequest {
  Optional<Snowflake> getAround();

  Optional<Snowflake> getBefore();

  Optional<Snowflake> getAfter();

  Optional<Long> getLimit();
}
