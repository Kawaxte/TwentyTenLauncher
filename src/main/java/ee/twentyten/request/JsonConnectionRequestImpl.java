package ee.twentyten.request;

import ee.twentyten.log.ELevel;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class JsonConnectionRequestImpl extends JsonConnectionRequest {

  @Override
  public JSONObject perform(URL url, EMethod method, Map<String, String> header) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.openHttpsConnection(url);
      connection.setRequestMethod(method.name());

      for (Map.Entry<String, String> entry : header.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    } catch (ProtocolException pe) {
      LoggerUtils.logMessage("Failed to set request method", pe, ELevel.ERROR);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return RequestUtils.getJsonResponse(connection);
  }

  @Override
  public JSONObject perform(URL url, EMethod method, Map<String, String> header, Object data) {
    HttpsURLConnection connection = null;
    try {
      connection = RequestUtils.openHttpsConnection(url);
      connection.setRequestMethod(method.name());

      for (Map.Entry<String, String> entry : header.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }

      connection.setDoOutput(true);
      try (OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream())) {
        osw.write(data.toString());
      }
    } catch (ProtocolException pe) {
      LoggerUtils.logMessage("Failed to set request method", pe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to write data to output stream", ioe, ELevel.ERROR);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return RequestUtils.getJsonResponse(connection);
  }
}
