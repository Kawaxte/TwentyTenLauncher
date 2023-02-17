package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HttpsConnectionRequestImpl extends HttpsConnectionRequest {

  private final HttpsConnectionResponse connectionResponse;

  {
    this.connectionResponse = new HttpsConnectionResponse();
  }

  @Override
  public HttpsURLConnection performRequest(URL url, EMethod method,
      Map<String, String> headers) {
    HttpsURLConnection connection = null;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method.name());
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError("Failed to set request method", pe, true);
    }
    return this.connectionResponse.response(connection);
  }

  @Override
  public HttpsURLConnection performRequest(URL url, EMethod method,
      Map<String, String> headers, Object data) {
    HttpsURLConnection connection = null;
    try {
      connection = this.openConnection(url);
      connection.setRequestMethod(method.name());
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }

      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(
          os)) {
        osw.write(data.toString());
      }
    } catch (ProtocolException pe) {
      LoggerHelper.logError("Failed to set request method", pe, true);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to write data to output stream", ioe, true);
    }
    return this.connectionResponse.response(connection);
  }
}
