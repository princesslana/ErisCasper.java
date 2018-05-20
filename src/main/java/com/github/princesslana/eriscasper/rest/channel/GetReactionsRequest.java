package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/channel#get-reactions-query-string-params">
 *     https://discordapp.com/developers/docs/resources/channel#get-reactions-query-string-params</a>
 */
@Value.Immutable
public interface GetReactionsRequest {
  Optional<Snowflake> getBefore();

  Optional<Snowflake> getAfter();

  Optional<Long> getLimit();

  default String toQueryString() {
    return new QueryStringBuilder()
        .addSnowflake("before", getBefore())
        .addSnowflake("after", getAfter())
        .addLong("limit", getLimit())
        .build();
  }
}
