package ee.twentyten.request;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

abstract class HttpsConnectionRequest {

  public abstract HttpsURLConnection perform(URL url, EMethod method, EHeader header);

  public abstract HttpsURLConnection perform(URL url, EMethod method, EHeader header,
      Object data);
}
