package io.github.kawaxte.twentyten.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Iterator;
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
  protected LinkedProperties linkedDefaults;

  {
    this.linkedMap = new LinkedHashMap<>();
  }

  public LinkedProperties() {
    this(null);
  }

  public LinkedProperties(LinkedProperties defaults) {
    this.linkedDefaults = defaults;
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
  public String getProperty(String key) {
    val oval = this.linkedMap.get(key);
    val sval = oval instanceof String
        ? (String) oval
        : null;
    return ((sval == null)
        && (this.linkedDefaults != null))
        ? this.linkedDefaults.getProperty(key)
        : sval;
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    val val = this.getProperty(key);
    return (val == null)
        ? defaultValue
        : val;
  }

  @Override
  public void list(PrintStream out) {
    out.println("-- listing linked properties --");
    synchronized (this) {
      for (val entry : this.linkedMap.entrySet()) {
        val key = entry.getKey();
        val val = entry.getValue();

        val sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        sb.append(val instanceof String
            ? (((String) val).length() > 40)
            ? (MessageFormat.format("{0}...",
            ((String) val).substring(0, 37)))
            : val : val);
        out.println(sb);
      }
    }
  }

  @Override
  public void list(PrintWriter out) {
    out.println("-- listing linked properties --");
    synchronized (this) {
      for (val entry : this.linkedMap.entrySet()) {
        val key = entry.getKey();
        val val = entry.getValue();

        val sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        sb.append(val instanceof String
            ? (((String) val).length() > 40)
            ? (MessageFormat.format("{0}...",
            ((String) val).substring(0, 37)))
            : val : val);
        out.println(sb);
      }
    }
  }

  @Override
  public synchronized Enumeration<Object> keys() {
    return new Enumeration<Object>() {
      private final Iterator<Object> iterator;

      {
        this.iterator = LinkedProperties.this.linkedMap.keySet().iterator();
      }

      @Override
      public boolean hasMoreElements() {
        return this.iterator.hasNext();
      }

      @Override
      public Object nextElement() {
        return this.iterator.next();
      }
    };
  }

  @Override
  public synchronized Enumeration<Object> elements() {
    return new Enumeration<Object>() {
      private final Iterator<Object> iterator;

      {
        this.iterator = LinkedProperties.this.linkedMap.values().iterator();
      }

      @Override
      public boolean hasMoreElements() {
        return this.iterator.hasNext();
      }

      @Override
      public Object nextElement() {
        return this.iterator.next();
      }
    };
  }

  @Override
  public synchronized boolean isEmpty() {
    return this.linkedMap.isEmpty();
  }

  @Override
  public synchronized int size() {
    return this.linkedMap.size();
  }

  @Override
  public synchronized void clear() {
    this.linkedMap.clear();
  }

  @Override
  public Set<Object> keySet() {
    return this.linkedMap.keySet();
  }
}
