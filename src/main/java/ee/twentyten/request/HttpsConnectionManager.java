package ee.twentyten.request;

import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

abstract class HttpsConnectionManager {

  public abstract HttpsURLConnection openConnection(URL url);

  public abstract HttpsURLConnection performRequest(URL url, String method,
      Map<String, String> headers, boolean cached);

  public abstract HttpsURLConnection performRequest(URL url, String method,
      Map<String, String> headers, boolean cached, String data);

  public abstract JSONObject performJsonRequest(URL url, String method, Map<String, String> headers,
      boolean cached);

  public abstract JSONObject performJsonRequest(URL url, String method, Map<String, String> headers,
      boolean cached, JSONObject data);
}
