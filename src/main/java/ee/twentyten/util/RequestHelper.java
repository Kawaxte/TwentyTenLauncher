package ee.twentyten.util;

import ee.twentyten.request.EMethod;
import ee.twentyten.request.HttpsConnectionRequestImpl;
import ee.twentyten.request.JsonConnectionRequestImpl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public final class RequestHelper {

  private static final HttpsConnectionRequestImpl HTTPS_CONNECTION_REQUEST;
  private static final JsonConnectionRequestImpl JSON_CONNECTION_REQUEST;
  public static Map<String, String> xWwwFormUrlencodedHeader;
  public static Map<String, String> jsonHeader;

  static {
    HTTPS_CONNECTION_REQUEST = new HttpsConnectionRequestImpl();
    JSON_CONNECTION_REQUEST = new JsonConnectionRequestImpl();

    xWwwFormUrlencodedHeader = Collections.unmodifiableMap(
        new HashMap<String, String>() {
          {
            put("Content-Type", "application/x-www-form-urlencoded");
            put("Accept", "application/x-www-form-urlencoded");
          }
        });
    jsonHeader = Collections.unmodifiableMap(
        new HashMap<String, String>() {
          {
            put("Content-Type", "application/json");
            put("Accept", "application/json");
          }
        });
  }

  private RequestHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static HttpsURLConnection performHttpsRequest(
      String url,
      EMethod method,
      Map<String, String> headers
  ) {
    try {
      URL httpsUrl = new URL(url);
      return HTTPS_CONNECTION_REQUEST.performRequest(
          httpsUrl, method, headers
      );
    } catch (MalformedURLException murle) {
      LoggerHelper.logError(
          "Failed to perform Https request",
          murle, true
      );
    }
    return null;
  }

  public static HttpsURLConnection performHttpsRequest(
      String url,
      EMethod method,
      Map<String, String> headers,
      Object data
  ) {
    try {
      URL httpsUrl = new URL(url);
      return HTTPS_CONNECTION_REQUEST.performRequest(
          httpsUrl, method, headers, data
      );
    } catch (MalformedURLException murle) {
      LoggerHelper.logError(
          "Failed to perform Https request",
          murle, true
      );
    }
    return null;
  }

  public static JSONObject performJsonRequest(
      String url,
      EMethod method,
      Map<String, String> headers
  ) {
    try {
      URL jsonUrl = new URL(url);
      return JSON_CONNECTION_REQUEST.performRequest(
          jsonUrl, method, headers
      );
    } catch (MalformedURLException murle) {
      LoggerHelper.logError(
          "Failed to perform JSON request",
          murle, true
      );
    }
    return null;
  }

  public static JSONObject performJsonRequest(
      String url,
      EMethod method,
      Map<String, String> headers,
      Object data
  ) {
    try {
      URL jsonUrl = new URL(url);
      return JSON_CONNECTION_REQUEST.performRequest(
          jsonUrl, method, headers, data
      );
    } catch (MalformedURLException murle) {
      LoggerHelper.logError(
          "Failed to perform JSON request",
          murle, true
      );
    }
    return null;
  }
}
