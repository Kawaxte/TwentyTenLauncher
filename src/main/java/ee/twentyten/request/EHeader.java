package ee.twentyten.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EHeader {
  X_WWW_FORM_URLENCODED {
    @Override
    public Map<String, String> getHeader() {
      return Collections.unmodifiableMap(EHeader.X_WWW_FORM_URLENCODED_HEADER);
    }
  }, JSON {
    @Override
    public Map<String, String> getHeader() {
      return Collections.unmodifiableMap(EHeader.JSON_HEADER);
    }
  }, NO_CACHE {
    @Override
    public Map<String, String> getHeader() {
      return Collections.unmodifiableMap(EHeader.NO_CACHE_HEADER);
    }
  };

  public static final Map<String, String> X_WWW_FORM_URLENCODED_HEADER = new HashMap<String, String>() {
    {
      put("Content-Type", "application/x-www-form-urlencoded");
      put("Accept", "application/x-www-form-urlencoded");
    }
  };

  public static final Map<String, String> JSON_HEADER = new HashMap<String, String>() {
    {
      put("Content-Type", "application/json");
      put("Accept", "application/json");
    }
  };

  public static final Map<String, String> NO_CACHE_HEADER = new HashMap<String, String>() {
    {
      put("Cache-Control", "no-cache");
      put("Pragma", "no-cache");
    }
  };

  public abstract Map<String, String> getHeader();
}