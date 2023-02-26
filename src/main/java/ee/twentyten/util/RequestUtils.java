package ee.twentyten.util;

import ee.twentyten.log.ELogger;
import ee.twentyten.request.ERequestHeader;
import ee.twentyten.request.ERequestMethod;
import ee.twentyten.request.HttpsConnectionRequestImpl;
import ee.twentyten.request.JsonConnectionRequestImpl;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
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
      LoggerUtils.log("Failed to create SSL context", gse, ELogger.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to open HTTPS connection", ioe, ELogger.ERROR);
    }
    return connection;
  }

  public static HttpsURLConnection performHttpsRequest(URL url, ERequestMethod method,
      ERequestHeader header) {
    return RequestUtils.httpsRequest.perform(url, method, header);
  }

  public static HttpsURLConnection performHttpsRequest(URL url, ERequestMethod method,
      ERequestHeader header, Object data) {
    return RequestUtils.httpsRequest.perform(url, method, header, data);
  }

  public static JSONObject performJsonRequest(URL url, ERequestMethod method,
      ERequestHeader header) {
    return RequestUtils.jsonRequest.perform(url, method, header);
  }

  public static JSONObject performJsonRequest(URL url, ERequestMethod method, ERequestHeader header,
      Object data) {
    return RequestUtils.jsonRequest.perform(url, method, header, data);
  }
}
