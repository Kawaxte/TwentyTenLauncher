package ee.twentyten.request;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.json.JSONObject;

public interface IJsonRequestService {

  JSONObject sendRequest(URL url, String method, Map<String, String> headers)
      throws IOException;

  JSONObject sendRequest(URL url, String method, Map<String, String> headers, JSONObject data)
      throws IOException;
}
