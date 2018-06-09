package com.github.princesslana.eriscasper.rest.emoji;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/emoji#modify-guild-emoji-json-params">
 *     https://discordapp.com/developers/docs/resources/emoji#modify-guild-emoji-json-params</a>
 */
@Value.Immutable
public interface ModifyGuildEmojiRequest {
  String getName();

  ImmutableList<Snowflake> getRoles();
}
