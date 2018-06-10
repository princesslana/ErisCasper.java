package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.util.QueryStringBuilder;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#create-guild-ban-query-string-params">
 *     https://discordapp.com/developers/docs/resources/guild#create-guild-ban-query-string-params</a>
 */
@Value.Immutable
public interface CreateGuildBanRequest {
  Integer getDeleteMessageDays();

  String getReason();

  default String toQueryString() {
    return new QueryStringBuilder()
        .add("reason", getReason())
        .add("delete_message_days", getDeleteMessageDays().toString())
        .build();
  }
}
