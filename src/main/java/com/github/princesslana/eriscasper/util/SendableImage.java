package com.github.princesslana.eriscasper.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public abstract class SendableImage {

  BufferedImage image;

  @Override
  public String toString() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "jpg", baos);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
    byte[] bytes = baos.toByteArray();
    return new String(Base64.getEncoder().encode(bytes));
  }
}
