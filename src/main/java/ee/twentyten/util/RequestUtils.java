package ee.twentyten.util;

import ee.twentyten.log.ELevel;
import ee.twentyten.request.EMethod;
import ee.twentyten.request.HttpsConnectionRequestImpl;
import ee.twentyten.request.JsonConnectionRequestImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.json.JSONObject;

public final class RequestUtils {

  private static HttpsConnectionRequestImpl httpsRequest;
  private static JsonConnectionRequestImpl jsonRequest;

  static {
    RequestUtils.httpsRequest = new HttpsConnectionRequestImpl();
    RequestUtils.jsonRequest = new JsonConnectionRequestImpl();
  }

  private RequestUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection getResponse(HttpsURLConnection connection) {
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = MessageFormat.format("{0} {1} ({2})", responseCode,
          responseMessage, connectionUrl);
      LoggerUtils.logMessage(formattedResponse,
          responseCode / 100 != 2 ? ELevel.ERROR : ELevel.INFO);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to get HTTPS response", ioe, ELevel.ERROR);
    }
    return connection;
  }

  public static JSONObject getJsonResponse(HttpsURLConnection connection) {
    String jsonResponse = null;
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = MessageFormat.format("{0} {1} ({2})", responseCode,
          responseMessage, connectionUrl);
      LoggerUtils.logMessage(formattedResponse,
          responseCode / 100 != 2 ? ELevel.ERROR : ELevel.INFO);

      try (InputStreamReader isr = new InputStreamReader(
          responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
          StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        jsonResponse = sb.toString();
      }
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to get HTTPS response", ioe, ELevel.ERROR);
    }

    Objects.requireNonNull(jsonResponse, "jsonResponse == null!");
    return jsonResponse.isEmpty() ? new JSONObject() : new JSONObject(jsonResponse);
  }

  public static URL createURL(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
    return null;
  }

  public static HttpsURLConnection openHttpsConnection(URL url) {
    HttpsURLConnection connection = null;
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());

      connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());

      connection.setConnectTimeout(15000);
      connection.setReadTimeout(60000);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.logMessage("Failed to create SSL context", gse, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to open HTTPS connection", ioe, ELevel.ERROR);
    }
    return connection;
  }

  public static HttpsURLConnection performHttpsRequest(URL url, EMethod method,
      Map<String, String> header) {
    return RequestUtils.httpsRequest.perform(url, method, header);
  }

  public static HttpsURLConnection performHttpsRequest(URL url, EMethod method,
      Map<String, String> header, Object data) {
    return RequestUtils.httpsRequest.perform(url, method, header, data);
  }

  public static JSONObject performJsonRequest(URL url, EMethod method,
      Map<String, String> header) {
    return RequestUtils.jsonRequest.perform(url, method, header);
  }

  public static JSONObject performJsonRequest(URL url, EMethod method, Map<String, String> header,
      Object data) {
    return RequestUtils.jsonRequest.perform(url, method, header, data);
  }
}
