package ee.twentyten.request;

import ee.twentyten.log.ELogger;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class JsonConnectionRequestImpl extends JsonConnectionRequest {

  private JSONObject getResponse(HttpsURLConnection connection) {
    String jsonResponse = null;
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = String.format("%d %s (%s)", responseCode, responseMessage,
          connectionUrl);
      LoggerUtils.log(formattedResponse, responseCode / 100 != 2 ? ELogger.ERROR : ELogger.INFO);

      try (InputStreamReader isr = new InputStreamReader(
          responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
          StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        jsonResponse = sb.toString();
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to get HTTPS response", ioe, ELogger.ERROR);
    }

    Objects.requireNonNull(jsonResponse, "jsonResponse == null!");
    return jsonResponse.isEmpty() ? new JSONObject() : new JSONObject(jsonResponse);
  }

  @Override
  public JSONObject perform(URL url, ERequestMethod method, ERequestHeader header) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.openHttpsConnection(url);
      connection.setRequestMethod(method.name());

      Map<String, String> headers = header.setHeader();
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    } catch (ProtocolException pe) {
      LoggerUtils.log("Failed to set request method", pe, ELogger.ERROR);
    }
    return this.getResponse(connection);
  }

  @Override
  public JSONObject perform(URL url, ERequestMethod method, ERequestHeader header, Object data) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.openHttpsConnection(url);
      connection.setRequestMethod(method.name());

      Map<String, String> headers = header.setHeader();
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }

      connection.setDoOutput(true);
      try (OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream())) {
        osw.write(data.toString());
      }
    } catch (ProtocolException pe) {
      LoggerUtils.log("Failed to set request method", pe, ELogger.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to write data to output stream", ioe, ELogger.ERROR);
    }
    return this.getResponse(connection);
  }
}
