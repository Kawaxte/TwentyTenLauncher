package ee.twentyten.util;

import ee.twentyten.request.EMethod;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import sun.security.util.Optional;

public final class FileHelper {

  public static long cacheExpirationTime;
  public static File workingDirectory;

  static {
    FileHelper.cacheExpirationTime = 86400000L;

    FileHelper.workingDirectory = LauncherHelper.getWorkingDirectory();
  }

  private FileHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Optional<BufferedImage> readImageFile(Class<?> clazz,
      String name) {
    try {
      URL input = clazz.getClassLoader().getResource(name);
      Objects.requireNonNull(input, "input == null!");
      return Optional.of(ImageIO.read(input));
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to read image file", ioe, true);
    }
    return Optional.empty();
  }

  public static Optional<JSONObject> readJsonFile(File src) {
    if (!src.exists() || !src.isFile()) {
      return Optional.empty();
    }

    try {
      byte[] bytes = Files.readAllBytes(src.toPath());
      String json = new String(bytes, StandardCharsets.UTF_8);
      return Optional.of(new JSONObject(json));
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to read bytes from file", ioe, true);
    }
    return Optional.empty();
  }

  public static File createDirectory(File parent, String name) {
    File directory = new File(parent, name);
    if (directory.exists() && directory.isDirectory()) {
      return directory;
    }

    boolean isDirectoryCreated = directory.mkdirs();
    if (!isDirectoryCreated) {
      LoggerHelper.logError("Failed to create directory", true);
      return null;
    }
    return directory;
  }

  public static void deleteDirectory(File directory) {
    if (!directory.exists()) {
      return;
    }

    File[] files = directory.listFiles();
    Objects.requireNonNull(files, "files == null!");
    for (File file : files) {
      if (file.isDirectory()) {
        FileHelper.deleteDirectory(file);
      } else {
        boolean isFileDeleted = file.delete();
        if (!isFileDeleted) {
          LoggerHelper.logError("Failed to delete file", true);
        }
      }
    }

    boolean isDirectoryDeleted = directory.delete();
    if (!isDirectoryDeleted) {
      LoggerHelper.logError("Failed to delete directory", true);
    }
  }

  public static void downloadFile(String url, final File target) {
    HttpsURLConnection connection = RequestHelper.performHttpsRequest(url,
        EMethod.GET, RequestHelper.xWwwFormUrlencodedHeader);
    Objects.requireNonNull(connection, "connection == null!");

    final CountDownLatch latch = new CountDownLatch(1);

    try {
      final InputStream is = connection.getInputStream();

      Thread downloadThread = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Files.copy(is, target.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException ioe) {
            LoggerHelper.logError("Failed to copy input stream to file", ioe,
                true);
          } finally {
            latch.countDown();
          }
        }
      }, "downloadFile");
      downloadThread.start();

      latch.await();
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get input stream", ioe, true);
    } catch (InterruptedException ie) {
      LoggerHelper.logError("Failed to wait for download thread", ie, true);
    }
  }
}
