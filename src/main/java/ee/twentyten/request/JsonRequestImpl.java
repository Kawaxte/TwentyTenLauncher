package ee.twentyten.request;

import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.RequestManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class JsonRequestImpl implements IJsonRequestService {

  private JSONObject getResponse(HttpsURLConnection connection) throws IOException {
    int responseCode = connection.getResponseCode();
    String responseMessage = connection.getResponseMessage();
    String connectionUrl = connection.getURL().toString();
    String response;
    try (InputStreamReader isr = new InputStreamReader(
        responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
        StandardCharsets.UTF_8)) {
      StringBuilder sb = new StringBuilder();
      char[] buffer = new char[1024];
      int read;
      while ((read = isr.read(buffer)) != -1) {
        sb.append(buffer, 0, read);
      }
      response = sb.toString();
    }

    if (responseCode / 100 == 2) {
      if (response.isEmpty()) {
        DebugLoggingManager.logWarn(this.getClass(),
            String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
        return new JSONObject();
      }
      DebugLoggingManager.logInfo(this.getClass(),
          String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
    } else {
      DebugLoggingManager.logError(this.getClass(),
          String.format("%d %s (%s)", responseCode, responseMessage, connectionUrl));
    }
    return response.charAt(0) != '{' ? new JSONObject() : new JSONObject(response);
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
        os.write(String.valueOf(data).getBytes(StandardCharsets.UTF_8));
      }
    }

    DebugLoggingManager.logInfo(this.getClass(), String.format("%s (%s)", method, url));

    RequestManager.enforceProtocol(connection);
    return this.getResponse(connection);
  }
}
