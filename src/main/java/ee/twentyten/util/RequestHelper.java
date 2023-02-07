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
  private static final Class<RequestHelper> CLASS_REF;
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
    
    CLASS_REF = RequestHelper.class;
  }

  private RequestHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection performRequest(String url, String method,
      Map<String, String> headers, boolean cached) {
    try {
      return CONNECTION_IMPL.performRequest(new URL(url), method, headers, cached);
    } catch (MalformedURLException murle) {
      LogHelper.logError(CLASS_REF, "Failed to create URL", murle);
    }
    return null;
  }

  public static HttpsURLConnection performRequest(String url, String method,
      Map<String, String> headers, boolean cached, String data) {
    try {
      return CONNECTION_IMPL.performRequest(new URL(url), method, headers, cached, data);
    } catch (MalformedURLException murle) {
      LogHelper.logError(CLASS_REF, "Failed to perform Https request", murle);
    }
    return null;
  }

  public static void performJsonRequest(String url, String method, Map<String, String> headers,
      boolean cached) {
    try {
      CONNECTION_IMPL.performJsonRequest(new URL(url), method, headers, cached);
    } catch (MalformedURLException murle) {
      LogHelper.logError(CLASS_REF, "Failed to perform JSON request", murle);
    }
  }

  public static JSONObject performJsonRequest(String url, String method,
      Map<String, String> headers, boolean cached, JSONObject data) {
    try {
      return CONNECTION_IMPL.performJsonRequest(new URL(url), method, headers, cached, data);
    } catch (MalformedURLException murle) {
      LogHelper.logError(CLASS_REF, "Failed to perform JSON request", murle);
    }
    return null;
  }
}
