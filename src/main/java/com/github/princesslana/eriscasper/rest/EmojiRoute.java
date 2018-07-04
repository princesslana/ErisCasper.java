package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.request.CreateGuildEmojiRequest;
import com.github.princesslana.eriscasper.data.request.ModifyGuildEmojiRequest;
import com.github.princesslana.eriscasper.data.resource.Emoji;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;

public class EmojiRoute {

  private final Snowflake id;

  private EmojiRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/emoji#list-guild-emojis">
   *     https://discordapp.com/developers/docs/resources/emoji#list-guild-emojis</a>
   */
  public Route<Void, ImmutableList<Emoji>> listGuildEmojis() {
    return Route.get(path(""), Route.jsonArrayResponse(Emoji.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/emoji#get-guild-emoji">
   *     https://discordapp.com/developers/docs/resources/emoji#get-guild-emoji</a>
   */
  public Route<Void, Emoji> getGuildEmoji(Snowflake emojiId) {
    return Route.get(path("/%s", emojiId.unwrap()), Emoji.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji">
   *     https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji</a>
   */
  public Route<CreateGuildEmojiRequest, Emoji> createGuildEmoji() {
    return Route.post(path(""), Emoji.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/emoji#modify-guild-emoji">
   *     https://discordapp.com/developers/docs/resources/emoji#modify-guild-emoji</a>
   */
  public Route<ModifyGuildEmojiRequest, Emoji> modifyGuildEmoji(Snowflake emojiId) {
    return Route.patch(path("/%s", emojiId.unwrap()), Emoji.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/emoji#delete-guild-emoji">
   *     https://discordapp.com/developers/docs/resources/emoji#delete-guild-emoji</a>
   */
  public Route<Void, Void> deleteGuildEmoji(Snowflake emojiId) {
    return Route.delete(path("/%s", emojiId.unwrap()), Void.class);
  }

  private String path(String fmt, String... args) {
    return "/guilds/" + id.unwrap() + "/emojis" + String.format(fmt, Arrays.asList(args).toArray());
  }

  public static EmojiRoute on(Snowflake id) {
    return new EmojiRoute(id);
  }

  public static EmojiRoute on(Guild guild) {
    return on(guild.getId());
  }
}
