package com.github.princesslana.eriscasper.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class EmojiImage extends SendableImage {

  public EmojiImage(BufferedImage image) {
    Image tmp = image.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
    this.image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = this.image.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();
  }
}
