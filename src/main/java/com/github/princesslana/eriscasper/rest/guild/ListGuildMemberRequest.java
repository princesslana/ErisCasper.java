package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#list-guild-members-query-string-params">
 *     https://discordapp.com/developers/docs/resources/guild#list-guild-members-query-string-params</a>
 */
@Value.Immutable
public interface ListGuildMemberRequest {
  Optional<Long> getLimit();

  Optional<Snowflake> getAfter();

  default String toQueryString() {
    return new QueryStringBuilder()
        .addSnowflake("after", getAfter())
        .addLong("limit", getLimit())
        .build();
  }
}
