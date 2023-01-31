package ee.twentyten.request;

import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.RequestManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class JsonRequestImpl implements IJsonRequest {

  private JSONObject getResponse(HttpsURLConnection connection) throws IOException {
    int responseCode = connection.getResponseCode();
    String responseMessage = connection.getResponseMessage();
    String connectionUrl = connection.getURL().toString();
    String response;
    try (InputStream is = responseCode >= 400 ? connection.getErrorStream()
        : connection.getInputStream()) {
      response = IOUtils.toString(is, StandardCharsets.UTF_8);
    }
    DebugLoggingManager.logInfo(this.getClass(),
        String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
    return response.isEmpty() ? new JSONObject() : new JSONObject(response);
  }

  @Override
  public JSONObject sendRequest(URL url, String method, Map<String, String> headers)
      throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }

    DebugLoggingManager.logInfo(this.getClass(), String.format("%s (%s)", method, url));

    RequestManager.enforceProtocol(connection);
    return this.getResponse(connection);
  }

  @Override
  public JSONObject sendRequest(URL url, String method, Map<String, String> headers,
      JSONObject data) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    if (method.equals("POST") || method.equals("PUT")) {
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        os.write(String.valueOf(data).getBytes());
      }
    }

    DebugLoggingManager.logInfo(this.getClass(), String.format("%s (%s)", method, url));

    RequestManager.enforceProtocol(connection);
    return this.getResponse(connection);
  }
}
