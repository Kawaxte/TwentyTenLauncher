package ee.twentyten.utils;

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
  }

  private static HttpsURLConnection getHttpResponse(HttpsURLConnection connection) {
    return connection;
  }

  private static JSONObject getJsonResponse(HttpsURLConnection connection) throws IOException {
    InputStream is = connection.getResponseCode() >= 400 ? connection.getErrorStream()
        : connection.getInputStream();

    String response = IOUtils.toString(is, StandardCharsets.UTF_8);
    return response.isEmpty() ? new JSONObject() : new JSONObject(response);
  }

  public static HttpsURLConnection requestHttpGet(String url)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
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
  }

  public static HttpsURLConnection requestHttpHead(String url)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
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
  }

  public static JSONObject requestJsonGet(String url)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
    SSLContext context = SSLContext.getInstance("TLSv1.2");
    context.init(null, null, null);

    HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
    connection.setSSLSocketFactory(context.getSocketFactory());
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/json");

    return getJsonResponse(connection);
  }

  public static JSONObject requestJsonPost(String url, JSONObject data)
      throws IOException, KeyManagementException, NoSuchAlgorithmException {
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
  }
}
