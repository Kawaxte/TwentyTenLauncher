package ee.twentyten.util;

import ee.twentyten.log.ELogger;
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

  public static BufferedImage readImageResource(String name, Class<?> clazz) {
    URL imageFileInput = clazz.getClassLoader().getResource(name);
    if (imageFileInput != null) {
      try {
        return ImageIO.read(imageFileInput);
      } catch (IOException ioe) {
        LoggerUtils.log("Failed to read image resource", ioe, ELogger.ERROR);
      }
    }
    return null;
  }

  public static String readFileContents(String fileName) {
    StringBuilder sb = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(
        fileName); InputStreamReader isr = new InputStreamReader(fis,
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

  public static JSONObject readJsonFileContents(File f) {
    try {
      byte[] fileBytes = Files.readAllBytes(f.toPath());
      String json = new String(fileBytes, StandardCharsets.UTF_8);
      return new JSONObject(json);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to read JSON file contents", ioe, ELogger.ERROR);
    }
    return new JSONObject();
  }

  public static void downloadFile(URL url, File f) {
    HttpsURLConnection connection = RequestUtils.performHttpsRequest(url, ERequestMethod.GET,
        ERequestHeader.X_WWW_FORM_URLENCODED);
    try (InputStream is = connection.getInputStream()) {
      Files.copy(is, f.toPath());
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to download file", ioe, ELogger.ERROR);
    }
  }
}
