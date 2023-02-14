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

  HttpsURLConnection openConnection(
      URL url
  ) {
    HttpsURLConnection connection = null;
    try {

      /* Open connection to the URL */
      connection = (HttpsURLConnection) url.openConnection();

      /* Force TLSv1.2 because Java 7 doesn't support TLSv1.3 and TLSv1.2 is
       * not enabled by default */
      SSLContext context = SSLContext.getInstance("TLSv1.2");
      context.init(null, null, new SecureRandom());

      /* Set the connection to use TLSv1.2 */
      connection.setSSLSocketFactory(context.getSocketFactory());
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to open connection",
          ioe, true
      );
    } catch (GeneralSecurityException gse) {
      LoggerHelper.logError(
          "Failed to initialise SSL context",
          gse, true
      );
    }
    return connection;
  }

  public abstract JSONObject performRequest(
      URL url,
      EMethod method,
      Map<String, String> headers
  );

  public abstract JSONObject performRequest(
      URL url,
      EMethod method,
      Map<String, String> headers,
      Object data
  );
}
