package com.github.princesslana.eriscasper.rest.user;

import com.github.princesslana.eriscasper.data.Snowflake;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/user#create-dm-json-params">
 *     https://discordapp.com/developers/docs/resources/user#create-dm-json-params</a>
 */
@Value.Immutable
public interface CreateDmRequest {
  Snowflake getRecipientId();

  static CreateDmRequest ofRecipientId(Snowflake userId) {
    return ImmutableCreateDmRequest.builder().recipientId(userId).build();
  }
}
