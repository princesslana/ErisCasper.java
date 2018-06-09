package com.github.princesslana.eriscasper.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public final class SendableImage {

  private SendableImage() {}

  public static String imageToBase64(File file) throws IOException {
    InputStream stream = file.toURI().toURL().openConnection().getInputStream();
    byte[] bytes = new byte[stream.available()];
    stream.read(bytes);
    return translateBase64(bytes);
  }

  public static String imageToBase64(BufferedImage image, FormatType type) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    type.write(image, baos);
    byte[] bytes = baos.toByteArray();
    return translateBase64(bytes);
  }

  private static String translateBase64(byte[] bytes) {
    return new String(Base64.getEncoder().encode(bytes));
  }
}
