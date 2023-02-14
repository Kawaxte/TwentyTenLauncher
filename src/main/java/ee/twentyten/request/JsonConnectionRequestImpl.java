package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class JsonConnectionRequestImpl extends JsonConnectionRequest {

  private final JsonConnectionResponse connectionResponse;

  {
    connectionResponse = new JsonConnectionResponse();
  }

  @Override
  public JSONObject performRequest(
      URL url,
      EMethod method,
      Map<String, String> headers
  ) {
    HttpsURLConnection connection = null;
    try {

      /* Open a connection to the URL */
      connection = this.openConnection(url);

      /* Set the request method */
      connection.setRequestMethod(method.name());

      /* Loop through the headers and set them */
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError(
          "Failed to set request method",
          pe, true
      );
    }
    return this.connectionResponse.getResponse(connection);
  }

  @Override
  public JSONObject performRequest(
      URL url,
      EMethod method,
      Map<String, String> headers,
      Object data
  ) {
    HttpsURLConnection connection = null;
    try {

      /* Open a connection to the URL */
      connection = this.openConnection(url);

      /* Set the request method */
      connection.setRequestMethod(method.name());

      /* Loop through the headers and set them */
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }

      /* Set the connection to output in order to write data to it */
      connection.setDoOutput(true);

      /* Write the data to the output stream */
      try (OutputStream os = connection.getOutputStream();
          OutputStreamWriter osw = new OutputStreamWriter(os)) {
        osw.write(data.toString());
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError(
          "Failed to set request method",
          pe, true
      );
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to write data to output stream",
          ioe, true
      );
    }
    return this.connectionResponse.getResponse(connection);
  }
}
