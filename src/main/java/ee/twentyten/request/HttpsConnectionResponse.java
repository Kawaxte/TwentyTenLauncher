package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;

class HttpsConnectionResponse {

  HttpsURLConnection getResponse(HttpsURLConnection connection) {
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = String.format("%d %s (%s)", responseCode,
          responseMessage, connectionUrl);

      if (responseCode / 100 == 2) {
        LoggerHelper.logInfo(formattedResponse, false);
      } else {
        LoggerHelper.logError(formattedResponse, false);
      }
      return connection;
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get Https response", ioe, true);
    }
    return null;
  }
}
