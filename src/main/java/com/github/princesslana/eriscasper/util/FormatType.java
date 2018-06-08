package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public enum FormatType {
  GIF("gif"),
  JPG("jpg"),
  JPEG("jpeg"),
  PNG("png");

  private final String type;

  FormatType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void write(BufferedImage image, OutputStream stream) {
    try {
      ImageIO.write(image, type, stream);
    } catch (IOException e) {
      throw new ErisCasperFatalException("Failed to process image of type `" + type + "`.");
    }
  }
}
