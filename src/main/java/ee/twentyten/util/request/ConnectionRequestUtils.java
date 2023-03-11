package ee.twentyten.util.request;

import ee.twentyten.log.ELevel;
import ee.twentyten.util.log.LoggerUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONObject;

public final class ConnectionRequestUtils {

  public static final Map<String, String> X_WWW_FORM_URLENCODED;
  public static final Map<String, String> JSON;
  public static final Map<String, String> NO_CACHE;

  static {
    X_WWW_FORM_URLENCODED = new HashMap<String, String>() {
      {
        put("Content-Type", "application/x-www-form-urlencoded");
        put("Accept", "application/x-www-form-urlencoded");
      }
    };
    JSON = new HashMap<String, String>() {
      {
        put("Content-Type", "application/json");
        put("Accept", "application/json");
      }
    };
    NO_CACHE = new HashMap<String, String>() {
      {
        put("Cache-Control", "no-cache");
        put("Pragma", "no-cache");
      }
    };
  }

  private ConnectionRequestUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection getHttpsResponse(HttpsURLConnection connection) {
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
    String response = null;
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
          sb.append(line.trim());
        }
        response = sb.toString().isEmpty() ? "{}" : sb.toString();
      }

      JSONObject jsonResponse = new JSONObject(response);
      String[] sensitiveKeys = {"accessToken", "access_token", "refresh_token", "Token"};
      for (String key : sensitiveKeys) {
        if (response.contains(key)) {
          jsonResponse.put(key, "[REDACTED]");
        }
      }
      LoggerUtils.logMessage(jsonResponse.toString(2),
          responseCode / 100 != 2 ? ELevel.ERROR : ELevel.INFO);
    } catch (NullPointerException npe) {
      LoggerUtils.logMessage("Failed to get JSON response", npe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to get input stream from JSON response", ioe, ELevel.ERROR);
    }
    return (response != null && !response.isEmpty()) ? new JSONObject(response) : null;
  }

  public static SSLSocketFactory getSSLSocketFactory() {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());
      return context.getSocketFactory();
    } catch (GeneralSecurityException gse) {
      LoggerUtils.logMessage("Failed to create SSL context", gse, ELevel.ERROR);
    }
    return null;
  }

  public static URL createURLFromString(String verificationUri) {
    try {
      return new URL(verificationUri);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to create URL from string", ioe, ELevel.ERROR);
    }
    return null;
  }
}
