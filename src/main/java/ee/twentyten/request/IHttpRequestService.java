package ee.twentyten.request;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public interface IHttpRequestService {

  HttpsURLConnection sendRequest(URL url, String method, Map<String, String> headers)
      throws IOException;

  HttpsURLConnection sendRequest(URL url, String method, Map<String, String> headers,
      String data) throws IOException;
}
