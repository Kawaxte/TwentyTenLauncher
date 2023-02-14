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

  public static Optional<BufferedImage> readImageFile(
      Class<?> clazz,
      String name
  ) {
    try {

      /* Get the URL of the image file. */
      URL input = clazz.getClassLoader().getResource(name);
      Objects.requireNonNull(
          input, "input == null!"
      );

      /* Read the image file and return an Optional containing the image. */
      return Optional.of(ImageIO.read(input));
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to read image file",
          ioe, true
      );
    }
    return Optional.empty();
  }

  public static Optional<JSONObject> readJsonFile(
      File src
  ) {

    /* If the file does not exist or is not a file, return an empty Optional. */
    if (!src.exists() || !src.isFile()) {
      return Optional.empty();
    }

    try {

      /* Read the file's contents into a byte array. */
      byte[] bytes = Files.readAllBytes(src.toPath());

      /* Convert the byte array to a string. */
      String json = new String(bytes, StandardCharsets.UTF_8);

      /* Create a new JSONObject from the string and return an Optional
       * containing the JSONObject. */
      JSONObject jsonObject = new JSONObject(json);
      return Optional.of(jsonObject);
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to read bytes from file",
          ioe, true
      );
    }
    return Optional.empty();
  }

  public static File createDirectory(
      File parent,
      String name
  ) {

    /* If the directory already exists, return it. */
    File directory = new File(parent, name);
    if (directory.exists() && directory.isDirectory()) {
      return directory;
    }

    /* Create the directory. */
    boolean isDirectoryCreated = directory.mkdirs();
    if (!isDirectoryCreated) {
      LoggerHelper.logError(
          "Failed to create directory",
          true
      );
      return null;
    }
    return directory;
  }

  public static void deleteDirectory(
      File directory
  ) {

    /* If the directory does not exist, return. */
    if (!directory.exists()) {
      return;
    }

    /* Get the directory's contents. */
    File[] files = directory.listFiles();
    Objects.requireNonNull(
        files, "files == null!"
    );

    /* Delete the directory's contents. */
    for (File file : files) {
      if (file.isDirectory()) {
        FileHelper.deleteDirectory(file);
      } else {
        boolean isFileDeleted = file.delete();
        if (!isFileDeleted) {
          LoggerHelper.logError(
              "Failed to delete file",
              true
          );
        }
      }
    }

    /* Delete the directory. */
    boolean isDirectoryDeleted = directory.delete();
    if (!isDirectoryDeleted) {
      LoggerHelper.logError(
          "Failed to delete directory",
          true
      );
    }
  }

  public static void downloadFile(
      String url,
      File target
  ) {

    /* Open a connection to the URL. */
    HttpsURLConnection connection = RequestHelper.performHttpsRequest(
        url,
        EMethod.GET,
        RequestHelper.xWwwFormUrlencodedHeader
    );
    Objects.requireNonNull(
        connection, "connection == null!"
    );

    /* Get the connection's input stream. */
    try (InputStream is = connection.getInputStream()) {

      /* Copy the file to the target file. */
      Files.copy(
          is,
          target.toPath(),
          StandardCopyOption.REPLACE_EXISTING
      );
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to download file",
          ioe, true
      );
    }
  }
}
