package ee.twentyten.util;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public final class FilesManager {

  public static final String VERSIONS_JSON_URL;
  public static final long CACHE_EXPIRATION_TIME = 86400000L;

  static {
    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/sojlabjoi/AlphacraftLauncher/master/versions.json";
  }

  private FilesManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Image readImageFile(Class<?> clazz, String name) {
    URL input = clazz.getClassLoader().getResource(name);
    Objects.requireNonNull(input);
    try {
      return ImageIO.read(input);
    } catch (IOException ioe) {
      return new ImageIcon(new byte[768]).getImage();
    }
  }

  public static JSONObject readJsonFile(File src) throws IOException {
    if (!src.exists() || !src.isFile()) {
      throw new FileNotFoundException(String.format("Can't find file in %s", src));
    }

    byte[] bytes = Files.readAllBytes(src.toPath());
    String json = new String(bytes, StandardCharsets.UTF_8);
    return new JSONObject(json);
  }

  public static void downloadFile(String url, File src) {
    try (InputStream is = Objects.requireNonNull(
            RequestManager.sendHttpRequest(url, "GET", RequestManager.X_WWW_FORM_HEADER), "is == null!")
        .getInputStream()) {
      FileUtils.copyInputStreamToFile(is, src);

      DebugLoggingManager.logInfo(FilesManager.class,
          String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe) {
      DebugLoggingManager.logError(FilesManager.class,
          String.format("Failed to download file from %s", url), ioe);
    }
  }

  public static void moveFile(File src, File dest) {
    try {
      FileUtils.moveFile(src, dest);
    } catch (IOException ioe1) {
      try (FileOutputStream fos = new FileOutputStream(dest)) {
        FileUtils.copyFile(src, fos);

        DebugLoggingManager.logInfo(FilesManager.class,
            String.format("\"%s\"", dest.getAbsolutePath()));
        boolean deleted = src.delete();
        if (!deleted) {
          throw new IOException(
              String.format("Failed to delete file from %s", src.getAbsolutePath()));
        }
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(FilesManager.class,
            String.format("Failed to move file from %s to %s", src.getAbsolutePath(),
                dest.getAbsolutePath()), ioe2);
      }
    }
  }

  public static void deleteFile(File src) {
    try {
      FileUtils.delete(src);

      DebugLoggingManager.logInfo(FilesManager.class,
          String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe1) {
      try {
        FileUtils.forceDelete(src);
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(FilesManager.class,
            String.format("Failed to delete file from %s", src.getAbsolutePath()), ioe2);
      }
    }
  }
}
