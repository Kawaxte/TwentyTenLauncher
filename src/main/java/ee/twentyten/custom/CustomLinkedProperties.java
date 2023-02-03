package ee.twentyten.custom;

import ee.twentyten.util.DebugLoggingManager;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CustomLinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private final LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

  @Override
  public synchronized Object put(Object key, Object value) {
    map.put(key, value);
    return super.put(key, value);
  }

  @Override
  public Set<Object> keySet() {
    return map.keySet();
  }

  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    return map.entrySet();
  }

  @Override
  public void store(OutputStream out, String comments) {
    try {
      if (comments != null) {
        out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        out.write("# ".getBytes(StandardCharsets.UTF_8));
        out.write(comments.getBytes(StandardCharsets.UTF_8));
        out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
      }

      Set<Map.Entry<Object, Object>> entries = entrySet();
      for (Map.Entry<Object, Object> entry : entries) {
        String key = (String) entry.getKey();
        out.write(key.getBytes(StandardCharsets.UTF_8));
        out.write("=".getBytes(StandardCharsets.UTF_8));

        String value = (String) entry.getValue();
        out.write(value.getBytes(StandardCharsets.UTF_8));
        out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException ioe) {
      DebugLoggingManager.logError(this.getClass(), "Failed to store properties", ioe);
    }
  }
}
