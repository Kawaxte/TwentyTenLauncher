package ee.twentyten.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EHeader {
  X_WWW_FORM_URLENCODED {
    @Override
    public Map<String, String> setHeader() {
      return Collections.unmodifiableMap(new HashMap<String, String>() {
        {
          put("Content-Type", "application/x-www-form-urlencoded");
          put("Accept", "application/x-www-form-urlencoded");
        }
      });
    }
  }, JSON {
    @Override
    public Map<String, String> setHeader() {
      return Collections.unmodifiableMap(new HashMap<String, String>() {
        {
          put("Content-Type", "application/json");
          put("Accept", "application/json");
        }
      });
    }
  },
  NO_CACHE {
    @Override
    public Map<String, String> setHeader() {
      return Collections.unmodifiableMap(new HashMap<String, String>() {
        {
          put("Cache-Control", "no-cache");
          put("Pragma", "no-cache");
        }
      });
    }
  };

  public abstract Map<String, String> setHeader();
}
