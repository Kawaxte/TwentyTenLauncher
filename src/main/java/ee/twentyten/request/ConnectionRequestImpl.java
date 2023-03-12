package ee.twentyten.request;

import ee.twentyten.log.ELevel;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class ConnectionRequestImpl extends ConnectionRequest {

  @Override
  public HttpsURLConnection performHttpsRequest() {
    HttpsURLConnection connection = null;
    try {
      connection = (HttpsURLConnection) this.url.openConnection();
      connection.setConnectTimeout(this.connectTimeout);
      connection.setReadTimeout(this.readTimeout);
      connection.setSSLSocketFactory(this.sslSocketFactory);
      connection.setUseCaches(this.useCaches);
      connection.setRequestMethod(this.method.name());
      if (this.header != null) {
        for (String key : this.header.keySet()) {
          connection.setRequestProperty(key, this.header.get(key));
        }
      }
      if (this.body != null) {
        connection.setDoOutput(true);
        try (OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream())) {
          osw.write(String.valueOf(this.body));
        }
      }
    } catch (NullPointerException npe) {
      LoggerUtils.logMessage("Failed to open HTTPS connection", npe, ELevel.ERROR);
    } catch (ProtocolException pe) {
      LoggerUtils.logMessage("Failed to set request method", pe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to write data to output stream", ioe, ELevel.ERROR);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return connection == null ? null : RequestUtils.getHttpsResponse(connection);
  }

  @Override
  public JSONObject performJsonRequest() {
    HttpsURLConnection connection = null;
    try {
      connection = (HttpsURLConnection) this.url.openConnection();
      connection.setConnectTimeout(this.connectTimeout);
      connection.setReadTimeout(this.readTimeout);
      connection.setSSLSocketFactory(this.sslSocketFactory);
      connection.setUseCaches(this.useCaches);
      connection.setRequestMethod(this.method.name());
      if (this.header != null) {
        for (String key : this.header.keySet()) {
          connection.setRequestProperty(key, this.header.get(key));
        }
      }
      if (this.body != null) {
        connection.setDoOutput(true);
        try (OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream())) {
          osw.write(String.valueOf(this.body));
        }
      }
    } catch (NullPointerException npe) {
      LoggerUtils.logMessage("Failed to open JSON connection", npe, ELevel.ERROR);
    } catch (ProtocolException pe) {
      LoggerUtils.logMessage("Failed to set request method", pe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to write data to output stream", ioe, ELevel.ERROR);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return connection == null ? null : RequestUtils.getJsonResponse(connection);
  }
}
