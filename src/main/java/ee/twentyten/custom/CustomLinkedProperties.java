package ee.twentyten.custom;

import ee.twentyten.log.ELogger;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.SystemUtils;
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
  private final LinkedHashMap<Object, Object> linkedMap;

  {
    this.linkedMap = new LinkedHashMap<>();
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    this.linkedMap.put(key, value);
    return super.put(key, value);
  }

  @Override
  public Set<Object> keySet() {
    return this.linkedMap.keySet();
  }

  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    return this.linkedMap.entrySet();
  }

  @Override
  public void store(OutputStream os, String comments) {
    try {
      if (comments != null) {
        String comment = String.format("%s# %s%s", SystemUtils.lineSeparator, comments,
            SystemUtils.lineSeparator);
        os.write(comment.getBytes(StandardCharsets.UTF_8));
      }

      Set<Map.Entry<Object, Object>> entries = entrySet();
      for (Map.Entry<Object, Object> entry : entries) {
        String key = (String) entry.getKey();
        os.write(key.getBytes(StandardCharsets.UTF_8));
        os.write("=".getBytes(StandardCharsets.UTF_8));

        String value = (String) entry.getValue();
        os.write(value.getBytes(StandardCharsets.UTF_8));
        os.write(SystemUtils.lineSeparator.getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to store properties", ioe, ELogger.ERROR);
    }
  }
}
