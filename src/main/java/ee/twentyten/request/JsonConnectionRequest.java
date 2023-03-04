package ee.twentyten.request;

import java.net.URL;
import java.util.Map;
import org.json.JSONObject;

abstract class JsonConnectionRequest {

  public abstract JSONObject perform(URL url, EMethod method, Map<String, String> header);

  public abstract JSONObject perform(URL url, EMethod method, Map<String, String> header,
      Object data);
}
