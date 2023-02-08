package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.json.JSONObject;

public class HttpsConnectionImpl extends HttpsConnectionManager {

  private final HttpsConnectionResponse connectionResponse;

  {
    this.connectionResponse = new HttpsConnectionResponse();
  }

  private void enforceProtocol(HttpsURLConnection connection) {
    try {
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());

      connection.setSSLSocketFactory(context.getSocketFactory());
    } catch (NoSuchAlgorithmException nsae) {
      LoggerHelper.logError("Failed to get instance of SSLContext", nsae, true);
    } catch (KeyManagementException kme) {
      LoggerHelper.logError("Failed to initialise SSLContext", kme, true);
    }
  }

  @Override
  public HttpsURLConnection openConnection(URL url) {
    HttpsURLConnection connection;
    try {
      connection = (HttpsURLConnection) url.openConnection();
      connection.setConnectTimeout(15000);
      connection.setReadTimeout(30000);

      this.enforceProtocol(connection);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to open connection", ioe, true);
      return null;
    }
    return connection;
  }

  @Override
  public HttpsURLConnection performRequest(URL url, String method, Map<String, String> headers,
      boolean cached) {
    HttpsURLConnection connection;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method);

      Objects.requireNonNull(headers, "headers == null!");
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
      if (cached) {
        connection.setUseCaches(true);
        connection.addRequestProperty("Cache-Control", "max-age=0");
      } else {
        connection.setUseCaches(false);
        connection.addRequestProperty("Cache-Control", "no-cache");
        connection.addRequestProperty("Pragma", "no-cache");
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError("Failed to set request method", pe, true);
      return null;
    }
    return this.connectionResponse.getResponse(connection);
  }

  @Override
  public HttpsURLConnection performRequest(URL url, String method, Map<String, String> headers,
      boolean cached, String data) {
    HttpsURLConnection connection;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method);

      Objects.requireNonNull(headers, "headers == null!");
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
      if (cached) {
        connection.setUseCaches(true);
        connection.addRequestProperty("Cache-Control", "max-age=0");
      } else {
        connection.setUseCaches(false);
        connection.addRequestProperty("Cache-Control", "no-cache");
        connection.addRequestProperty("Pragma", "no-cache");
      }

      Objects.requireNonNull(data, "data == null!");
      if (method.equals("POST") || method.equals("PUT")) {
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
          os.write(data.getBytes(StandardCharsets.UTF_8));
        }
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError("Failed to set request method", pe, true);
      return null;
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to write to output stream", ioe, true);
      return null;
    }

    return this.connectionResponse.getResponse(connection);
  }

  @Override
  public JSONObject performJsonRequest(URL url, String method, Map<String, String> headers,
      boolean cached) {
    HttpsURLConnection connection;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method);

      Objects.requireNonNull(headers, "headers == null!");
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
      if (cached) {
        connection.setUseCaches(true);
        connection.addRequestProperty("Cache-Control", "max-age=0");
      } else {
        connection.setUseCaches(false);
        connection.addRequestProperty("Cache-Control", "no-cache");
        connection.addRequestProperty("Pragma", "no-cache");
      }
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to open Https connection", ioe, true);
      return null;
    }
    return this.connectionResponse.getJsonResponse(connection);
  }

  @Override
  public JSONObject performJsonRequest(URL url, String method, Map<String, String> headers,
      boolean cached, JSONObject data) {
    HttpsURLConnection connection = null;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method);

      Objects.requireNonNull(headers, "headers == null!");
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
      if (cached) {
        connection.setUseCaches(true);
        connection.addRequestProperty("Cache-Control", "max-age=0");
      } else {
        connection.setUseCaches(false);
        connection.addRequestProperty("Cache-Control", "no-cache");
        connection.addRequestProperty("Pragma", "no-cache");
      }

      Objects.requireNonNull(data, "data == null!");
      if (method.equals("POST") || method.equals("PUT")) {
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
          os.write(String.valueOf(data).getBytes(StandardCharsets.UTF_8));
        }
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError("Failed to set request method", pe, true);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to write to output stream", ioe, true);
    }
    return this.connectionResponse.getJsonResponse(connection);
  }
}
