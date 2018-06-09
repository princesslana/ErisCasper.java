package com.github.princesslana.eriscasper.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public enum FormatType {
  GIF("gif"),
  JPEG("jpeg"),
  PNG("png");

  private final String type;

  FormatType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void write(BufferedImage image, OutputStream stream) throws IOException {
    ImageIO.write(image, type, stream);
  }
}
