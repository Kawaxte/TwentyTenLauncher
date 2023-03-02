package ee.twentyten.request;

import java.net.URL;
import org.json.JSONObject;

abstract class JsonConnectionRequest {

  public abstract JSONObject perform(URL url, EMethod method, EHeader header);

  public abstract JSONObject perform(URL url, EMethod method, EHeader header,
      Object data);
}
