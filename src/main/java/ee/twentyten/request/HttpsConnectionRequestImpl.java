package ee.twentyten.request;

import ee.twentyten.log.ELogger;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HttpsConnectionRequestImpl extends HttpsConnectionRequest {

  private HttpsURLConnection getResponse(HttpsURLConnection connection) {
    try {
      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      String connectionUrl = connection.getURL().toString();
      String formattedResponse = String.format("%d %s (%s)", responseCode, responseMessage,
          connectionUrl);
      LoggerUtils.log(formattedResponse, responseCode / 100 != 2 ? ELogger.ERROR : ELogger.INFO);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to get HTTPS response", ioe, ELogger.ERROR);
    }
    return connection;
  }

  @Override
  public HttpsURLConnection perform(URL url, ERequestMethod method, ERequestHeader header) {
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
  public HttpsURLConnection perform(URL url, ERequestMethod method, ERequestHeader header,
      Object data) {
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
