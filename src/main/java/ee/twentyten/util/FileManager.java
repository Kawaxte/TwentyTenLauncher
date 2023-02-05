package ee.twentyten.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.json.JSONObject;

public final class FileManager {

  public static final long CACHE_EXPIRATION_TIME;
  public static File workingDirectory;

  static {
    CACHE_EXPIRATION_TIME = 86400000L;

    workingDirectory = LauncherManager.getWorkingDirectory();
  }

  private FileManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Image readImageFile(Class<?> clazz, String name) {
    URL input = clazz.getClassLoader().getResource(name);
    Objects.requireNonNull(input);
    try {
      LoggingManager.logInfo(FileManager.class, String.format("\"%s\"", input));
      return ImageIO.read(input);
    } catch (IOException ioe) {
      LoggingManager.logError(FileManager.class,
          String.format("Failed to read image from \"%s\"", input), ioe);
      return new ImageIcon(new byte[768]).getImage();
    }
  }

  public static JSONObject readJsonFile(File src) {
    if (!src.exists() || !src.isFile()) {
      LoggingManager.logError(FileManager.class,
          String.format("File \"%s\" doesn't exist or isn't a file", src.getAbsolutePath()));
    }

    byte[] bytes;
    try {
      bytes = Files.readAllBytes(src.toPath());
    } catch (IOException ioe) {
      LoggingManager.logError(FileManager.class, "Failed to read bytes from file", ioe);
      return new JSONObject();
    }
    String json = new String(bytes, StandardCharsets.UTF_8);
    return new JSONObject(json);
  }

  public static void downloadFile(String url, File src) {
    try (InputStream is = Objects.requireNonNull(
            RequestManager.sendHttpRequest(url, "GET", RequestManager.X_WWW_FORM_HEADER), "is == null!")
        .getInputStream()) {
      Files.copy(is, src.toPath());

      LoggingManager.logInfo(FileManager.class,
          String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe) {
      LoggingManager.logError(FileManager.class,
          String.format("Failed to download file from \"%s\"", url), ioe);
    }
  }
}
