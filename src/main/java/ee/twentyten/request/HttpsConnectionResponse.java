package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;

class HttpsConnectionResponse {

  HttpsURLConnection getResponse(
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
      String formattedResponse = String.format("%d %s (%s)", responseCode,
          responseMessage, connectionUrl);

      /* Log using INFO level if the response code is 2xx, otherwise log
       * using ERROR level */
      if (responseCode / 100 == 2) {
        LoggerHelper.logInfo(formattedResponse, false);
      } else {
        LoggerHelper.logError(formattedResponse, false);
      }
      return connection;
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to get Https response",
          ioe, true
      );
    }
    return null;
  }
}
