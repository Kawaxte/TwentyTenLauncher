package ee.twentyten.request;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.json.JSONObject;

public abstract class JsonConnectionRequest {

  HttpsURLConnection openConnection(URL url) {
    HttpsURLConnection connection = null;
    try {

      /* Force TLSv1.2 because it is not enabled by default on Java 7 and is
       * minimum required protocol for majority of websites */
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());

      connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(context.getSocketFactory());
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to open connection", ioe, true);
    } catch (GeneralSecurityException gse) {
      LoggerHelper.logError("Failed to initialise SSL context", gse, true);
    }
    return connection;
  }

  public abstract JSONObject performRequest(URL url, EMethod method,
      Map<String, String> headers);

  public abstract JSONObject performRequest(URL url, EMethod method,
      Map<String, String> headers, Object data);
}
