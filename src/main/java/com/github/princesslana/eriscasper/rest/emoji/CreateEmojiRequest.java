package com.github.princesslana.eriscasper.rest.emoji;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.util.FormatType;
import com.github.princesslana.eriscasper.util.SendableImage;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji-json-params">
 *     https://discordapp.com/developers/docs/resources/emoji#create-guild-emoji-json-params</a>
 */
@Value.Immutable
public interface CreateEmojiRequest {
  String getName();

  String getImage();

  ImmutableList<Snowflake> getRoles();

  static String imageToString(File file) throws IOException {
    return SendableImage.imageToBase64(file);
  }

  static String imageToString(BufferedImage image, FormatType type) throws IOException {
    return SendableImage.imageToBase64(image, type);
  }
}
