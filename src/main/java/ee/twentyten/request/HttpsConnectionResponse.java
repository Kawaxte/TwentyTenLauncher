package ee.twentyten.request;

import ee.twentyten.util.LogHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

class HttpsConnectionResponse {

  private static final Class<HttpsConnectionResponse> CLASS_REF;

  static {
    CLASS_REF = HttpsConnectionResponse.class;
  }

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
        LogHelper.logInfo(CLASS_REF, formattedResponse);
      } else {
        LogHelper.logError(CLASS_REF, formattedResponse);
      }
    } catch (IOException ioe) {
      LogHelper.logError(CLASS_REF, "Failed to get Https response", ioe);
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
        LogHelper.logInfo(CLASS_REF, formattedResponse);
      } else {
        LogHelper.logError(CLASS_REF, formattedResponse);
      }
      return response.isEmpty() ? new JSONObject() : new JSONObject(response);
    } catch (IOException ioe) {
      LogHelper.logError(CLASS_REF, "Failed to get JSON response", ioe);
    }
    return null;
  }
}
