package ee.twentyten.util;

import ee.twentyten.request.HttpsConnectionImpl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public final class RequestHelper {

  private static final HttpsConnectionImpl CONNECTION_IMPL;
  public static Map<String, String> xWwwFormHeader;
  public static Map<String, String> jsonHeader;

  static {
    xWwwFormHeader = Collections.unmodifiableMap(new HashMap<String, String>() {{
      put("Content-Type", "application/x-www-form-urlencoded");
    }});
    jsonHeader = Collections.unmodifiableMap(new HashMap<String, String>() {{
      put("Content-Type", "application/json");
    }});

    CONNECTION_IMPL = new HttpsConnectionImpl();
  }

  private RequestHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection performRequest(String url, String method,
      Map<String, String> headers, boolean cached) {
    try {
      return CONNECTION_IMPL.performRequest(new URL(url), method, headers, cached);
    } catch (MalformedURLException murle) {
      LoggerHelper.logError("Failed to create URL", murle, true);
    }
    return null;
  }

  public static HttpsURLConnection performRequest(String url, String method,
      Map<String, String> headers, boolean cached, String data) {
    try {
      return CONNECTION_IMPL.performRequest(new URL(url), method, headers, cached, data);
    } catch (MalformedURLException murle) {
      LoggerHelper.logError("Failed to perform Https request", murle, true);
    }
    return null;
  }

  public static void performJsonRequest(String url, String method, Map<String, String> headers,
      boolean cached) {
    try {
      CONNECTION_IMPL.performJsonRequest(new URL(url), method, headers, cached);
    } catch (MalformedURLException murle) {
      LoggerHelper.logError("Failed to perform JSON request", murle, true);
    }
  }

  public static JSONObject performJsonRequest(String url, String method,
      Map<String, String> headers, boolean cached, JSONObject data) {
    try {
      return CONNECTION_IMPL.performJsonRequest(new URL(url), method, headers, cached, data);
    } catch (MalformedURLException murle) {
      LoggerHelper.logError("Failed to perform JSON request", murle, true);
    }
    return null;
  }
}
