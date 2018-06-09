package com.github.princesslana.eriscasper.rest.emoji;

import com.github.princesslana.eriscasper.data.immutable.Wrapped;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.github.princesslana.eriscasper.util.FormatType;
import com.github.princesslana.eriscasper.util.SendableImage;
import org.immutables.value.Value;

@Wrapped
@Value.Immutable
public interface EmojiImageWrapper extends Wrapper<String> {

  static EmojiImage of(File file) throws IOException {
    return EmojiImage.of(SendableImage.imageToBase64(file));
  }

  static EmojiImage of(BufferedImage image, FormatType type) throws IOException {
    return EmojiImage.of(SendableImage.imageToBase64(image, type));
  }
}
