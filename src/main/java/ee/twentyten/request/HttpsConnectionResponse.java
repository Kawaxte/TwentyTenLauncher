package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

class HttpsConnectionResponse {

  private int responseCode;
  private String responseMessage;
  private String connectionUrl;

  HttpsURLConnection getResponse(HttpsURLConnection connection) {
    try {
      this.responseCode = connection.getResponseCode();
      this.responseMessage = connection.getResponseMessage();
      this.connectionUrl = connection.getURL().toString();

      String formattedResponse = String.format("%d %s (%s)", this.responseCode,
          this.responseMessage, this.connectionUrl);
      if (this.responseCode / 100 == 2) {
        LoggerHelper.logInfo(formattedResponse, true);
      } else {
        LoggerHelper.logError(formattedResponse, true);
      }
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get Https response", ioe, true);
      return null;
    }
    return connection;
  }

  JSONObject getJsonResponse(HttpsURLConnection connection) {
    try {
      this.responseCode = connection.getResponseCode();
      this.responseMessage = connection.getResponseMessage();
      this.connectionUrl = connection.getURL().toString();

      String response;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
          this.responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
          StandardCharsets.UTF_8))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        response = sb.toString();
      }

      String formattedResponse = String.format("%d %s (%s)", this.responseCode,
          this.responseMessage, this.connectionUrl);
      if (this.responseCode / 100 == 2) {
        LoggerHelper.logInfo(formattedResponse, true);
      } else {
        LoggerHelper.logError(formattedResponse, true);
      }
      return response.isEmpty() ? new JSONObject() : new JSONObject(response);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get JSON response", ioe, true);
    }
    return null;
  }
}
