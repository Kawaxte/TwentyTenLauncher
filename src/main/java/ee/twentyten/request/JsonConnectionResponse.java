package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

class JsonConnectionResponse {

  JSONObject getResponse(HttpsURLConnection connection) {
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = String.format("%d %s (%s)", responseCode,
          responseMessage, connectionUrl);

      String response;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
          responseCode >= 400 ? connection.getErrorStream()
              : connection.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        response = sb.toString();
      }

      if (responseCode / 100 == 2) {
        LoggerHelper.logInfo(formattedResponse, false);
      } else {
        LoggerHelper.logError(formattedResponse, false);
      }
      return response.isEmpty() ? new JSONObject() : new JSONObject(response);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get JSON response", ioe, true);
    }
    return null;
  }
}
