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

  public static JSONObject readJsonFile(File f) throws IOException {
    if (!f.exists() || !f.isFile()) {
      throw new FileNotFoundException(String.format("Can't find file in %s", f));
    }

    byte[] bytes = Files.readAllBytes(f.toPath());
    String json = new String(bytes, StandardCharsets.UTF_8);
    return new JSONObject(json);
  }

  public static void downloadFile(String url, File srcPath) {
    try (InputStream is = Objects.requireNonNull(
            RequestManager.sendHttpRequest(url, "GET", RequestManager.X_WWW_FORM_HEADER), "is == null!")
        .getInputStream()) {
      FileUtils.copyInputStreamToFile(is, srcPath);
    } catch (IOException ioe) {
      DebugLoggingManager.logError(FilesManager.class,
          String.format("Failed to download file from %s", url), ioe);
    }
  }

  public static void moveFile(File srcPath, File destPath) {
    try {
      FileUtils.moveFile(srcPath, destPath);
    } catch (IOException ioe1) {
      try (FileOutputStream fos = new FileOutputStream(destPath)) {
        FileUtils.copyFile(srcPath, fos);

        boolean deleted = srcPath.delete();
        if (!deleted) {
          throw new IOException(
              String.format("Failed to delete file from %s", srcPath.getAbsolutePath()));
        }
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(FilesManager.class,
            String.format("Failed to move file from %s to %s", srcPath.getAbsolutePath(),
                destPath.getAbsolutePath()), ioe2);
      }
    }
  }

  public static void deleteFile(File srcPath) {
    try {
      FileUtils.delete(srcPath);
    } catch (IOException ioe1) {
      try {
        FileUtils.forceDelete(srcPath);
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(FilesManager.class,
            String.format("Failed to delete file from %s", srcPath.getAbsolutePath()), ioe2);
      }
    }
  }
}
