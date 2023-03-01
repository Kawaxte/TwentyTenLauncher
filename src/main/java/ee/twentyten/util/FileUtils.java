package ee.twentyten.util;

import ee.twentyten.log.ELoggerLevel;
import ee.twentyten.request.ERequestHeader;
import ee.twentyten.request.ERequestMethod;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public final class FileUtils {

  private FileUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static int getContentLength(URL url) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.performHttpsRequest(url, ERequestMethod.HEAD,
          ERequestHeader.NO_CACHE);
      return connection.getContentLength();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  public static String getFileNameFromUrl(URL url) {
    String urlPath = url.getPath();
    return urlPath.substring(urlPath.lastIndexOf('/') + 1);
  }

  public static BufferedImage readImageResource(String name, Class<?> clazz) {
    URL imageFileInput = clazz.getClassLoader().getResource(name);
    if (imageFileInput != null) {
      try {
        return ImageIO.read(imageFileInput);
      } catch (IOException ioe) {
        LoggerUtils.log("Failed to read image resource", ioe, ELoggerLevel.ERROR);
      }
    }
    return null;
  }

  public static String readFile(String fileName) {
    StringBuilder sb = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(
        fileName); InputStreamReader isr = new InputStreamReader(fis,
        StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to read file contents", ioe, ELoggerLevel.ERROR);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFile(File jsonFile) {
    try {
      byte[] fileBytes = Files.readAllBytes(jsonFile.toPath());
      String json = new String(fileBytes, StandardCharsets.UTF_8);
      return new JSONObject(json);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to read JSON file contents", ioe, ELoggerLevel.ERROR);
    }
    return new JSONObject();
  }

  public static void downloadFile(URL url, File targetFile) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.performHttpsRequest(url, ERequestMethod.GET,
          ERequestHeader.NO_CACHE);
      try (InputStream is = connection.getInputStream()) {
        Files.copy(is, targetFile.toPath());
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to download file", ioe, ELoggerLevel.ERROR);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}
