package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

class JsonConnectionResponse {

  JSONObject getResponse(
      HttpsURLConnection connection
  ) {
    try {

      /* Get the HTTP status code from the response */
      int responseCode = connection.getResponseCode();

      /* Get the HTTP status message from the response */
      String responseMessage = connection.getResponseMessage();

      /* Represent the URL of the connection as a string */
      String connectionUrl = connection.getURL().toString();

      /* Format the response code, message, and URL */
      String formattedResponse = String.format(
          "%d %s (%s)",
          responseCode, responseMessage, connectionUrl
      );


      /* Read the response body */
      String response;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
          responseCode >= 400
              ? connection.getErrorStream()
              : connection.getInputStream(),
          StandardCharsets.UTF_8))) {

        /* Use 'StringBuilder' to avoid creating a new 'String' object for
         * every line. */
        StringBuilder sb = new StringBuilder();

        /* Read the output of the process line by line. */
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        response = sb.toString();
      }

      if (responseCode / 100 == 2) {
        LoggerHelper.logInfo(
            formattedResponse,
            false
        );
      } else {
        LoggerHelper.logError(
            formattedResponse,
            false
        );
      }

      /* Return an empty JSON object if the response is empty, otherwise
       * return a JSON object with the response. */
      return response.isEmpty()
          ? new JSONObject()
          : new JSONObject(response);
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to get JSON response",
          ioe, true
      );
    }
    return null;
  }
}
