package com.github.princesslana.eriscasper.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public final class SendableImage {

  private SendableImage() {}

  public static String imageToBase64(BufferedImage image, FormatType type) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    type.write(image, baos);
    byte[] bytes = baos.toByteArray();
    return new String(Base64.getEncoder().encode(bytes));
  }
}
