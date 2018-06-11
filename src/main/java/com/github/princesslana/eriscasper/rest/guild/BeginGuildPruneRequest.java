package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#begin-guild-prune-query-string-params">
 *     https://discordapp.com/developers/docs/resources/guild#begin-guild-prune-query-string-params</a>
 */
@Value.Immutable
public interface BeginGuildPruneRequest {
  Long getDays();

  default String toQueryString() {
    return new QueryStringBuilder().addLong("days", getDays()).build();
  }

  static BeginGuildPruneRequest ofDays(long days) {
    return ImmutableBeginGuildPruneRequest.builder().days(days).build();
  }
}
