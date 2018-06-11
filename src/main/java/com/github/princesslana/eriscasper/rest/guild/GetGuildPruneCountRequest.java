package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#get-guild-prune-count-query-string-params">
 *     https://discordapp.com/developers/docs/resources/guild#get-guild-prune-count-query-string-params</a>
 */
@Value.Immutable
public interface GetGuildPruneCountRequest {
  Integer getDays();

  default String toQueryString() {
    return new QueryStringBuilder().add("days", getDays().toString()).build();
  }

  static GetGuildPruneCountRequest ofDays(int days) {
    return ImmutableGetGuildPruneCountRequest.builder().days(days).build();
  }
}
