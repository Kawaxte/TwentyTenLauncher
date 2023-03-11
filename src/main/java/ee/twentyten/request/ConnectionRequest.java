package ee.twentyten.request;

import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONObject;

public abstract class ConnectionRequest {

  protected URL url;
  protected EMethod method;
  protected Map<String, String> header;
  protected Object body;
  protected int connectTimeout = 15000;
  protected int readTimeout = 30000;
  protected SSLSocketFactory sslSocketFactory;
  protected boolean useCaches = true;

  public abstract HttpsURLConnection performHttpsRequest();

  public abstract JSONObject performJsonRequest();

  public static class Builder {

    private final ConnectionRequest request;

    {
      this.request = new ConnectionRequestImpl();
    }

    public Builder setUrl(URL url) {
      this.request.url = url;
      return this;
    }

    public Builder setMethod(EMethod method) {
      this.request.method = method;
      return this;
    }

    public Builder setHeaders(Map<String, String> header) {
      this.request.header = header;
      return this;
    }

    public Builder setBody(Object body) {
      this.request.body = body;
      return this;
    }

    public Builder setConnectTimeout(int timeout) {
      this.request.connectTimeout = timeout;
      return this;
    }

    public Builder setReadTimeout(int timeout) {
      this.request.readTimeout = timeout;
      return this;
    }

    public Builder setSSLSocketFactory(SSLSocketFactory factory) {
      this.request.sslSocketFactory = factory;
      return this;
    }

    public Builder setUseCaches(boolean useCaches) {
      this.request.useCaches = useCaches;
      return this;
    }

    public ConnectionRequest build() {
      return this.request;
    }
  }
}
