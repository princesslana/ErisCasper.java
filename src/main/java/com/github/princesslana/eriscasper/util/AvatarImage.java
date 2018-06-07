package com.github.princesslana.eriscasper.util;

import java.awt.image.BufferedImage;

public class AvatarImage extends SendableImage {

  public AvatarImage(BufferedImage image) {
    this.image = image;
  }

  public String getData() {
    return "data:image/jpeg;base64," + super.toString();
  }
}
