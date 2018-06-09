package com.github.princesslana.eriscasper.rest.auditlog;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log-query-string-parameters">
 *     https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log-query-string-parameters</a>
 */
@Value.Immutable
public interface GetGuildAuditLog {
  /*
  	user_id	snowflake	filter the log for a user id
  action_type	int	the type of audit log event
  before	snowflake	filter the log before a certain entry id
  limit	integer	how many entries are returned (default 50, minimum 1, maximum 100)
  	 */
  Optional<Snowflake> getUserId();

  Optional<Long> getType();

  Optional<Snowflake> getBefore();

  Optional<Long> getLimit();

  default String toQueryString() {
    return new QueryStringBuilder()
        .addSnowflake("user_id", getUserId())
        .addLong("action_type", getType())
        .addSnowflake("before", getBefore())
        .addLong("limit", getLimit())
        .build();
  }
}
