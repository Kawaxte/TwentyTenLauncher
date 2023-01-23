package ee.twentyten.utils;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;

public final class ImageManager {

  private ImageManager() {
  }

  public static Image readImage(Class<?> clazz, String name) throws IOException {
    URL input = clazz.getClassLoader().getResource(name);
    return ImageIO.read(Objects.requireNonNull(input));
  }
}
