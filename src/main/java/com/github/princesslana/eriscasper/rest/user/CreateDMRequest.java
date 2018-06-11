package com.github.princesslana.eriscasper.rest.user;

import com.github.princesslana.eriscasper.data.Snowflake;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/user#create-dm-json-params">
 *     https://discordapp.com/developers/docs/resources/user#create-dm-json-params</a>
 */
@Value.Immutable
public interface CreateDMRequest {
  Snowflake getRecipientId();

  static CreateDMRequest ofRecipientId(Snowflake userId) {
    return ImmutableCreateDMRequest.builder().recipientId(userId).build();
  }
}
