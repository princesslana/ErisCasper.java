package com.github.princesslana.eriscasper.rest.emoji;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji-json-params">
 *     https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji-json-params</a>
 */
@Value.Style(deepImmutablesDetection = true)
@Value.Immutable
public interface CreateEmojiRequest {
  String getName();

  EmojiImageWrapper getImage();

  ImmutableList<Snowflake> getRoles();
}
