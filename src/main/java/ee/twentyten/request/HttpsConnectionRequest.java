package ee.twentyten.request;

import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

abstract class HttpsConnectionRequest {

  public abstract HttpsURLConnection perform(URL url, EMethod method, Map<String, String> header);

  public abstract HttpsURLConnection perform(URL url, EMethod method, Map<String, String> header,
      Object data);
}
