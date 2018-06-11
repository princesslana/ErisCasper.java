package com.github.princesslana.eriscasper.rest.user;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/user#create-group-dm-json-params">
 *     https://discordapp.com/developers/docs/resources/user#create-group-dm-json-params</a>
 */
@Value.Immutable
public interface CreateGroupDMRequest {
  ImmutableList<String> getAccessTokens();

  ImmutableMap<Snowflake, String> getNicks();
}
