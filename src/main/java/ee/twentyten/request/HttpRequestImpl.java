package ee.twentyten.request;

import ee.twentyten.util.LoggingManager;
import ee.twentyten.util.RequestManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HttpRequestImpl implements IHttpRequestService {

  private HttpsURLConnection getResponse(HttpsURLConnection connection) throws IOException {
    int responseCode = connection.getResponseCode();
    String responseMessage = connection.getResponseMessage();
    String connectionUrl = connection.getURL().toString();

    if (responseCode / 100 == 2) {
      LoggingManager.logInfo(this.getClass(),
          String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
    } else {
      LoggingManager.logError(this.getClass(),
          String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
    }
    return connection;
  }

  @Override
  public HttpsURLConnection sendRequest(URL url, String method, Map<String, String> headers)
      throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }

    LoggingManager.logInfo(this.getClass(), String.format("%s (%s)", method, url));

    RequestManager.enforceProtocol(connection);
    return this.getResponse(connection);
  }


  @Override
  public HttpsURLConnection sendRequest(URL url, String method, Map<String, String> headers,
      String data) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    if (method.equals("POST") || method.equals("PUT")) {
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        os.write(data.getBytes(StandardCharsets.UTF_8));
      }
    }

    LoggingManager.logInfo(this.getClass(), String.format("%s (%s)", method, url));

    RequestManager.enforceProtocol(connection);
    return this.getResponse(connection);
  }
}
