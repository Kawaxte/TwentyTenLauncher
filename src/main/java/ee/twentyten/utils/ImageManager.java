package ee.twentyten.utils;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;

public final class ImageManager {

  private ImageManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Image readImage(Class<?> clazz, String name) {
    URL input = clazz.getClassLoader().getResource(name);
    Objects.requireNonNull(input);
    try {
      return ImageIO.read(input);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't read image from %s", input), e);
    }
  }
}
