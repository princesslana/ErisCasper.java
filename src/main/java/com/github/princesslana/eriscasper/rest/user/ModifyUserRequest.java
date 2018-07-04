package com.github.princesslana.eriscasper.rest.user;

import com.github.princesslana.eriscasper.util.FormatType;
import com.github.princesslana.eriscasper.util.SendableImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/user#modify-current-user-json-params">
 *     https://discordapp.com/developers/docs/resources/user#modify-current-user-json-params</a>
 */
@Value.Style
@Value.Immutable
public interface ModifyUserRequest {
  String getUsername();

  String getAvatar();

  static String avatarToString(File file, FormatType type) throws IOException {
    return format(SendableImage.imageToBase64(file), type);
  }

  static String avatarToString(BufferedImage image, FormatType type) throws IOException {
    return format(SendableImage.imageToBase64(image, type), type);
  }

  static String format(String base64, FormatType type) {
    return "data:image/" + type.getType() + ";base64," + base64;
  }
}
