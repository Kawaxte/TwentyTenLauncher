package ee.twentyten.request;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

abstract class HttpsConnectionRequest {

  public abstract HttpsURLConnection perform(URL url, ERequestMethod method, ERequestHeader header);

  public abstract HttpsURLConnection perform(URL url, ERequestMethod method, ERequestHeader header,
      Object data);
}
