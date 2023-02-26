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
public class LinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private final LinkedHashMap<Object, Object> linkedMap;

  {
    this.linkedMap = new LinkedHashMap<>();
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    String value = (String) this.linkedMap.get(key);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public synchronized Object setProperty(String key, String value) {
    if (value == null) {
      value = "";
    }
    if (value.matches("^[0-9]+$")) {
      return this.linkedMap.put(key, Integer.parseInt(value));
    }
    if (value.matches("^(true|false)$")) {
      return this.linkedMap.put(key, Boolean.parseBoolean(value));
    }
    return this.linkedMap.put(key, value);
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    super.put(key, value);
    return this.linkedMap.put(key, value);
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
        StringBuilder sbComment = new StringBuilder();
        sbComment.append(SystemUtils.lineSeparator);
        sbComment.append("# ");
        sbComment.append(comments);
        sbComment.append(SystemUtils.lineSeparator);
        os.write(sbComment.toString().getBytes(StandardCharsets.UTF_8));
      }

      Set<Map.Entry<Object, Object>> entries = entrySet();
      for (Map.Entry<Object, Object> entry : entries) {
        String key = (String) entry.getKey();
        os.write(key.getBytes(StandardCharsets.UTF_8));
        os.write("=".getBytes(StandardCharsets.UTF_8));

        Object value = entry.getValue();
        switch (value.getClass().getSimpleName()) {
          case "Boolean":
            Boolean boolValue = (Boolean) value;
            os.write(boolValue.toString().getBytes(StandardCharsets.UTF_8));
            break;
          case "Integer":
            Integer intValue = (Integer) value;
            os.write(intValue.toString().getBytes(StandardCharsets.UTF_8));
            break;
          case "String":
            String strValue = (String) value;
            os.write(strValue.getBytes(StandardCharsets.UTF_8));
            break;
          default:
            throw new IllegalArgumentException(String.format("%s=%s", key, value));
        }
        os.write(SystemUtils.lineSeparator.getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to write bytes to output stream", ioe, ELogger.ERROR);
    }
  }
}
