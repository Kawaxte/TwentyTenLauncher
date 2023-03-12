package ee.twentyten.util;

import ee.twentyten.log.ELevel;
import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
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

  public static String getFileName(URL url) {
    String urlPath = url.getPath();
    return urlPath.substring(urlPath.lastIndexOf('/') + 1);
  }

  public static BufferedImage readImageResource(String name, Class<?> clazz) {
    URL imageFileUrl = clazz.getClassLoader().getResource(name);
    if (imageFileUrl != null) {
      try {
        return ImageIO.read(imageFileUrl);
      } catch (IOException ioe) {
        LoggerUtils.logMessage("Failed to read image resource from URL", ioe, ELevel.ERROR);
      }
    }
    return null;
  }

  public static String readFileContents(String name) {
    StringBuilder sb = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(name);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to read file contents", ioe, ELevel.ERROR);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFileContents(File f) {
    try {
      byte[] fileBytes = Files.readAllBytes(f.toPath());
      String json = new String(fileBytes, StandardCharsets.UTF_8);
      return new JSONObject(json);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to read JSON file contents", ioe, ELevel.ERROR);
    }
    return new JSONObject();
  }

  public static void downloadFile(URL url, File f) {
    try {
      HttpsURLConnection connection = new ConnectionRequest.Builder()
          .setUrl(url)
          .setMethod(EMethod.GET)
          .setSSLSocketFactory(RequestUtils.getSSLSocketFactory())
          .build().performHttpsRequest();
      try (InputStream is = connection.getInputStream()) {
        Files.copy(is, f.toPath());
      }
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to download file from URL", ioe, ELevel.ERROR);
    }
  }
}
