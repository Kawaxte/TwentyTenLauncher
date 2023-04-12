package io.github.kawaxte.twentyten.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private final Map<Object, Object> linkedMap;

  {
    this.linkedMap = new LinkedHashMap<>();
  }

  @Override
  public synchronized Object setProperty(String key, String value) {
    super.put(key, value);
    return this.linkedMap.put(key, value);
  }

  @Override
  public void store(OutputStream os, String comments) throws IOException {
    if (comments != null) {
      val sb = new StringBuilder();
      sb.append(System.lineSeparator());
      sb.append("#\u00A0");
      sb.append(comments);
      sb.append(System.lineSeparator());
      os.write(sb.toString().getBytes());
    }

    synchronized (this) {
      for (val entry : this.linkedMap.entrySet()) {
        val key = entry.getKey();
        val value = entry.getValue();

        val sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (value != null) {
          sb.append(value);
        }
        sb.append(System.lineSeparator());
        os.write(sb.toString().getBytes());
      }
    }
    os.flush();
  }

  @Override
  public Set<Object> keySet() {
    return this.linkedMap.keySet();
  }
}
