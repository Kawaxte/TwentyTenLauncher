package ee.twentyten.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public final class RequestManager {

  private RequestManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static HttpsURLConnection getHttpResponse(HttpsURLConnection connection) {
    return connection;
  }

  private static JSONObject getJsonResponse(HttpsURLConnection connection) {
    try (InputStream is = connection.getResponseCode() >= 400 ? connection.getErrorStream()
        : connection.getInputStream()) {
      String response = IOUtils.toString(is, StandardCharsets.UTF_8);
      return response.isEmpty() ? new JSONObject() : new JSONObject(response);
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Can't read response from %s", connection.getURL().toString()), e);
    }
  }

  public static HttpsURLConnection requestHttpGet(String url) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, null);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty("Pragma", "no-cache");
      connection.setUseCaches(false);
      return getHttpResponse(connection);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to initialise SSL context", e);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't connect to %s", url), e);
    }
  }

  public static HttpsURLConnection requestHttpHead(String url) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, null);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());
      connection.setRequestMethod("HEAD");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty("Pragma", "no-cache");
      connection.setUseCaches(false);
      return getHttpResponse(connection);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to initialise SSL context", e);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't connect to %s", url), e);
    }
  }

  public static JSONObject requestJsonGet(String url) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, null);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      return getJsonResponse(connection);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to initialise SSL context", e);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't connect to %s", url), e);
    }
  }

  public static JSONObject requestJsonPost(String url, JSONObject data) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, null);

      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      try (OutputStream os = connection.getOutputStream()) {
        os.write(String.valueOf(data).getBytes());
      }
      return getJsonResponse(connection);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to initialise SSL context", e);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't connect to %s", url), e);
    }
  }
}
