package ee.twentyten.util;

import ee.twentyten.log.ELogger;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

public final class FileUtils {

  private FileUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static BufferedImage readImageResource(String name, Class<?> clazz) {
    URL input = clazz.getClassLoader().getResource(name);
    if (input != null) {
      try {
        return ImageIO.read(input);
      } catch (IOException ioe) {
        LoggerUtils.log("Failed to read image resource", ioe, ELogger.ERROR);
      }
    }
    return null;
  }

  public static String readFileContents(String filePath) {
    StringBuilder sb = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(
        filePath); InputStreamReader isr = new InputStreamReader(fis,
        StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to read file contents", ioe, ELogger.ERROR);
    }
    return sb.toString();
  }
}
