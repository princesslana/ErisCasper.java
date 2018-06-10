package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import java.util.Optional;
import jdk.internal.jline.internal.Nullable;
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

  static ListGuildMemberRequest withLimit(Long limit) {
    return withLimitAfter(limit, null);
  }

  static ListGuildMemberRequest after(Snowflake id) {
    return withLimitAfter(null, id);
  }

  static ListGuildMemberRequest withLimitAfter(@Nullable Long limit, @Nullable Snowflake after) {
    return ImmutableListGuildMemberRequest.builder()
        .limit(Optional.ofNullable(limit))
        .after(Optional.ofNullable(after))
        .build();
  }
}
