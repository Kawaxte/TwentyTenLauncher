package ee.twentyten.request;

import java.net.URL;
import org.json.JSONObject;

abstract class JsonConnectionRequest {

  public abstract JSONObject perform(URL url, ERequestMethod method, ERequestHeader header);

  public abstract JSONObject perform(URL url, ERequestMethod method, ERequestHeader header,
      Object data);
}
