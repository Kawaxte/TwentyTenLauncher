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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import org.json.JSONObject;

public final class FilesManager {

  public static final long CACHE_EXPIRATION_TIME = 86400000L;

  private FilesManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String[] getFilesFromUrl(String url) {
    List<String> fileList = new ArrayList<>();

    HttpsURLConnection connection = RequestManager.sendHttpRequest(url, "GET",
        RequestManager.X_WWW_FORM_HEADER);
    Objects.requireNonNull(connection, "connection == null!");
    if (connection.getContentType().startsWith("text/html")) {
      try {
        File f = new File(Objects.requireNonNull(connection.getURL().getFile()));
        String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");
        for (String line : lines) {
          if (line.contains("href=")) {
            int startIndex = line.indexOf("href=") + 6;
            int endIndex = line.indexOf("\"", startIndex);

            String file = line.substring(startIndex, endIndex);
            if (!file.endsWith("/")) {
              fileList.add(file);
            }
          }
        }
      } catch (IOException ioe) {
        DebugLoggingManager.logError(FilesManager.class,
            String.format("Failed to get files from %s", url), ioe);
      }
    }
    return fileList.toArray(new String[0]);
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
      Files.copy(is, src.toPath());

      DebugLoggingManager.logInfo(FilesManager.class,
          String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe) {
      DebugLoggingManager.logError(FilesManager.class,
          String.format("Failed to download file from %s", url), ioe);
    }
  }

  public static void moveFile(File src, File dest) {
    try {
      Files.move(src.toPath(), dest.toPath());
    } catch (IOException ioe1) {
      try (FileOutputStream fos = new FileOutputStream(dest)) {
        Files.copy(src.toPath(), fos);

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
      boolean deleted = src.delete();
      if (!deleted) {
        throw new IOException(
            String.format("Failed to delete file from %s", src.getAbsolutePath()));
      }

      DebugLoggingManager.logInfo(FilesManager.class,
          String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe) {
      src.deleteOnExit();

      DebugLoggingManager.logError(FilesManager.class,
          String.format("Failed to delete file from %s", src.getAbsolutePath()), ioe);
    }
  }
}
