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

  /**
   * Prevents instantiation of this class.
   *
   * @throws UnsupportedOperationException if this method is called.
   */
  private FileHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  /**
   * Reads an image file using the specified class and name.
   *
   * @param clazz The class to use to find the image file
   * @param name  The name of the image file
   * @return An optional that contains the image file's data as a
   * `BufferedImage` if it is found and successfully read, or an empty optional
   * if it is not found or there was a failure in reading the image file.
   */
  public static Optional<BufferedImage> readImageFile(
      Class<?> clazz,
      String name
  ) {

    /* Try to read the image file. */
    try {

      /* Get the URL of the image file. */
      URL input = clazz.getClassLoader().getResource(name);

      /* If the URL is not null, read the image file and return an Optional
       * containing the image file's data as a BufferedImage. */
      if (input != null) {
        return Optional.of(ImageIO.read(input));
      }
    } catch (IOException ioe) {

      /* Create a string for the error message. */
      String readError = "Failed to read image file";

      /* Log the error. */
      LoggerHelper.logError(readError, ioe, true);
    }
    return Optional.empty();
  }

  /**
   * Reads the contents of a JSON file.
   *
   * @param src The file to be read.
   * @return An Optional containing the JSONObject representation of the file if
   * the file exists and is readable, or an empty Optional if the file does not
   * exist or is not a file.
   */
  public static Optional<JSONObject> readJsonFile(
      File src
  ) {

    /* If the file does not exist or is not a file, return an empty Optional. */
    if (!src.exists() || !src.isFile()) {
      return Optional.empty();
    }

    /* Try to read the file's contents. */
    try {

      /* Read the file's contents into a byte array. */
      byte[] bytes = Files.readAllBytes(src.toPath());

      /* Convert the byte array to a string. */
      String json = new String(bytes, StandardCharsets.UTF_8);

      /* Return an Optional containing the JSONObject representation of the
       * file. */
      return Optional.of(new JSONObject(json));
    } catch (IOException ioe) {

      /* Create a string for the error message. */
      String readError = "Failed to read bytes from file";

      /* Log the error. */
      LoggerHelper.logError(readError, ioe, true);
    }
    return Optional.empty();
  }

  /**
   * Creates a directory at the specified location.
   *
   * @param parent the parent directory
   * @param name   the name of the directory
   * @return the newly created directory, or `null` if creation failed
   */
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

    /* If the directory was not created, log the error and return null. */
    if (!isDirectoryCreated) {
      Throwable t = new Throwable("Failed to create directory");

      LoggerHelper.logError(t.getMessage(), t, true);
      return null;
    }
    return directory;
  }

  /**
   * Deletes a directory and its contents recursively.
   *
   * @param directory The directory to be deleted.
   */
  public static void deleteDirectory(
      File directory
  ) {

    /* If the directory does not exist, return. */
    if (!directory.exists()) {
      return;
    }

    /* If the directory is not a directory, delete it and return. */
    File[] files = directory.listFiles();
    if (files == null) {
      String listError = "Failed to list files in directory";

      LoggerHelper.logError(listError, true);
      return;
    }

    /* Delete the directory's contents. */
    for (File file : files) {
      if (file.isDirectory()) {
        FileHelper.deleteDirectory(file);
      } else {
        boolean isFileDeleted = file.delete();
        if (!isFileDeleted) {
          Throwable t = new Throwable("Failed to delete file");

          LoggerHelper.logError(t.getMessage(), t, true);
        }
      }
    }

    /* Delete the directory. */
    boolean isDirectoryDeleted = directory.delete();

    /* If the directory was not deleted, log the error. */
    if (!isDirectoryDeleted) {
      Throwable t = new Throwable("Failed to delete directory");

      LoggerHelper.logError(t.getMessage(), t, true);
    }
  }

  /**
   * Downloads a file from a specified URL and saves it to the specified target
   * file.
   *
   * @param url    The URL of the file to be downloaded.
   * @param target The target file to save the downloaded file to.
   */
  public static void downloadFile(
      String url,
      File target
  ) {

    /* Open a connection to the URL. */
    HttpsURLConnection connection = RequestHelper.performHttpsRequest(
        url,
        EMethod.GET,
        RequestHelper.xWwwFormUrlencodedHeader);

    /* If the connection is null, log the error and return. */
    if (connection == null) {
      LoggerHelper.logError("Failed to establish connection", true);
      return;
    }

    /* Try to download the file. */
    try (InputStream is = connection.getInputStream()) {

      /* Copy the file to the target file. */
      Files.copy(
          is,
          target.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {

      /* Create a string for the error message. */
      String downloadError = String.format(
          "%s -> %s",
          url, target.getAbsolutePath());

      /* Log the error. */
      LoggerHelper.logError(downloadError, ioe, true);
    }
  }
}
