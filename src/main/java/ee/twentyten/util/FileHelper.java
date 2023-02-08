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
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public final class FileHelper {

  public static final long CACHE_EXPIRATION_TIME;
  public static File workingDirectory;

  static {
    CACHE_EXPIRATION_TIME = 86400000L;

    workingDirectory = LauncherHelper.getWorkingDirectory();
  }

  private FileHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Image readImageFile(Class<?> clazz, String name) {
    try {
      URL input = clazz.getClassLoader().getResource(name);
      if (input != null) {
        return ImageIO.read(input);
      }
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to read image file", ioe, true);
    }
    return null;
  }

  public static JSONObject readJsonFile(File src) {
    if (!src.exists() || !src.isFile()) {
      Throwable t = new Throwable(String.format("File \"%s\" doesn't exist or isn't a file",
          src.getAbsolutePath()));

      LoggerHelper.logError(t.getMessage(), t, true);
      return null;
    }

    byte[] bytes;
    try {
      bytes = Files.readAllBytes(src.toPath());
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to read bytes from file", ioe, true);
      return new JSONObject();
    }

    String json = new String(bytes, StandardCharsets.UTF_8);
    return new JSONObject(json);
  }

  public static void downloadFile(String url, File src) {
    HttpsURLConnection connection = RequestHelper.performRequest(url, "GET",
        RequestHelper.xWwwFormHeader, true);

    Objects.requireNonNull(connection, "connection == null!");
    try (InputStream is = connection.getInputStream()) {
      Files.copy(is, src.toPath());

      LoggerHelper.logInfo(String.format("\"%s\"", src.getAbsolutePath()), true);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to download file", ioe, true);
    }
  }

  public static File createDirectory(File parent, String name) {
    File directory = new File(parent, name);
    if (!directory.exists()) {
      boolean created = directory.mkdir();
      if (!created) {
        Throwable t = new Throwable("Failed to create directory");

        LoggerHelper.logError(t.getMessage(), t, true);
        return null;
      }
    }
    return directory;
  }

  public static void deleteDirectory(File directory) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            FileHelper.deleteDirectory(file);
          } else {
            boolean deleted = file.delete();
            if (!deleted) {
              Throwable t = new Throwable("Failed to delete file");

              LoggerHelper.logError(t.getMessage(), t, true);
              return;
            }
          }
        }
      }

      boolean deleted = directory.delete();
      if (!deleted) {
        Throwable t = new Throwable("Failed to delete directory");

        LoggerHelper.logError(t.getMessage(), t, true);
      }
    }
  }
}
