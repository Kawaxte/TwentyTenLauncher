package ee.twentyten.util;

import ee.twentyten.request.HttpRequestImpl;
import ee.twentyten.request.JsonRequestImpl;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.json.JSONObject;

public final class RequestManager {

  public static final Map<String, String> JSON_HEADER;
  public static final Map<String, String> X_WWW_FORM_HEADER;
  public static final Map<String, String> NO_CACHE_HEADER;
  private static final HttpRequestImpl HTTP_REQUEST;
  private static final JsonRequestImpl JSON_REQUEST;

  static {
    JSON_HEADER = Collections.unmodifiableMap(new HashMap<String, String>() {{
      put("Content-Type", "application/json; charset=UTF-8");
      put("Accept", "application/json");
    }});
    X_WWW_FORM_HEADER = Collections.unmodifiableMap(new HashMap<String, String>() {{
      put("Content-Type", "application/x-www-form-urlencoded");
      put("Accept", "application/x-www-form-urlencoded");
    }});
    NO_CACHE_HEADER = Collections.unmodifiableMap(new HashMap<String, String>() {{
      put("Cache-Control", "no-cache");
      put("Pragma", "no-cache");
    }});

    HTTP_REQUEST = new HttpRequestImpl();
    JSON_REQUEST = new JsonRequestImpl();
  }

  private RequestManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection sendHttpRequest(String url, String method,
      Map<String, String> headers) {
    try {
      URL requestUrl = new URL(url);
      return HTTP_REQUEST.sendRequest(requestUrl, method, headers);
    } catch (IOException ioe) {
      LoggingManager.logError(RequestManager.class, "Failed to send HTTP request", ioe);
    }
    return null;
  }

  public static HttpsURLConnection sendHttpRequest(String url, String method,
      Map<String, String> headers, String data) {
    try {
      URL requestUrl = new URL(url);
      return HTTP_REQUEST.sendRequest(requestUrl, method, headers, data);
    } catch (IOException ioe) {
      LoggingManager.logError(RequestManager.class, "Failed to send HTTP request", ioe);
    }
    return null;
  }

  public static JSONObject sendJsonRequest(String url, String method, Map<String, String> headers) {
    try {
      URL requestUrl = new URL(url);
      return JSON_REQUEST.sendRequest(requestUrl, method, headers);
    } catch (IOException ioe) {
      LoggingManager.logError(RequestManager.class, "Failed to send JSON request", ioe);
    }
    return null;
  }

  public static JSONObject sendJsonRequest(String url, String method, Map<String, String> headers,
      JSONObject data) {
    try {
      URL requestUrl = new URL(url);
      return JSON_REQUEST.sendRequest(requestUrl, method, headers, data);
    } catch (IOException ioe) {
      LoggingManager.logError(RequestManager.class, "Failed to send JSON request", ioe);
    }
    return null;
  }

  public static void enforceProtocol(HttpsURLConnection connection) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());

      connection.setSSLSocketFactory(context.getSocketFactory());
    } catch (GeneralSecurityException gse) {
      LoggingManager.logError(RequestManager.class, "Failed to enforce protocol", gse);
    }
  }
}
