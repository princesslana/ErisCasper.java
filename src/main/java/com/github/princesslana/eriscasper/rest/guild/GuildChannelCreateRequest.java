package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Overwrite;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#create-guild-channel-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#create-guild-channel-json-params</a>
 */
@Value.Immutable
public interface GuildChannelCreateRequest {
  String getName();

  Optional<Integer> getType();

  Optional<String> getTopic();

  Optional<Integer> getBitrate();

  Optional<Integer> getUserLimit();

  ImmutableList<Overwrite> getPermissionOverwrites();

  Optional<Snowflake> getParentId();

  Optional<Boolean> isNsfw();

  static GuildChannelCreateRequest ofName(String name) {
    return ImmutableGuildChannelCreateRequest.builder().name(name).build();
  }

  static GuildChannelCreateRequest ofNameAndCategory(String name, Snowflake category) {
    return ImmutableGuildChannelCreateRequest.builder().name(name).parentId(category).build();
  }
}
