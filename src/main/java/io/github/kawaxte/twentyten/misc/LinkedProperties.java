package io.github.kawaxte.twentyten.misc;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private Map<Object, Object> linkedMap;
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
    return this.linkedMap.put(key, value);
  }

  @Override
  public void store(Writer writer, String comments) throws IOException {
    if (comments != null) {
      val sb = new StringBuilder();
      sb.append(System.lineSeparator());
      sb.append("#");
      sb.append(comments);
      sb.append(System.lineSeparator());
      writer.write(sb.toString());
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
        writer.write(sb.toString());
      }
    }
    writer.flush();
  }

  @Override
  public String getProperty(String key) {
    val oval = this.linkedMap.get(key);
    val sval = (oval instanceof String)
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
        if (val instanceof String) {
          val sb = new StringBuilder();
          sb.append(key);
          sb.append("=");
          sb.append((((String) val).length() > 40)
              ? (MessageFormat.format("{0}...",
              ((String) val).substring(0, 37)))
              : val);
          out.println(sb);
        }
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
        if (val instanceof String) {
          val sb = new StringBuilder();
          sb.append(key);
          sb.append("=");
          sb.append((((String) val).length() > 40)
              ? (MessageFormat.format("{0}...",
              ((String) val).substring(0, 37)))
              : val);
          out.println(sb);
        }
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
  public synchronized boolean contains(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return this.linkedMap.entrySet().stream().map(entry ->
            entry.getValue() != null
                ? entry.getValue()
                : this.linkedDefaults.get(entry.getKey()))
        .anyMatch(Predicate.isEqual(value));
  }

  @Override
  public boolean containsValue(Object value) {
    return this.contains(value);
  }

  @Override
  public synchronized boolean containsKey(Object key) {
    return this.linkedMap.entrySet().stream().anyMatch(entry ->
        entry.getKey().hashCode() == key.hashCode()
            && entry.getKey().equals(key));
  }

  @Override
  public synchronized Object get(Object key) {
    return this.linkedMap.entrySet().stream().filter(entry ->
            entry.getKey().hashCode() == key.hashCode()
                && entry.getKey().equals(key))
        .findFirst()
        .map(Entry::getValue)
        .orElse(null);
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return this.linkedMap.entrySet().stream().filter(entry ->
            entry.getKey().hashCode() == key.hashCode()
                && entry.getKey().equals(key))
        .findFirst()
        .map(entry -> {
          val old = entry.getValue();
          entry.setValue(value);
          return old;
        })
        .orElse(null);
  }

  @Override
  public synchronized Object remove(Object key) {
    return this.linkedMap.entrySet().stream().filter(entry ->
            entry.getKey().hashCode() == key.hashCode()
                && entry.getKey().equals(key))
        .findFirst()
        .map(entry -> {
          val old = entry.getValue();
          this.linkedMap.remove(entry.getKey());
          return old;
        })
        .orElse(null);
  }

  @Override
  public synchronized void putAll(Map<?, ?> t) {
    t.forEach(this::put);
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
  public synchronized Object clone() {
    val clone = (LinkedProperties) super.clone();
    clone.linkedMap = new LinkedHashMap<>(this.linkedMap);
    return clone;
  }

  @Override
  public Set<Object> keySet() {
    return this.linkedMap.keySet();
  }

  @Override
  public Set<Entry<Object, Object>> entrySet() {
    if (this.linkedMap.entrySet().stream().anyMatch(entry ->
        !(entry.getKey() instanceof String)
            || !(entry.getValue() instanceof String))) {
      throw new ClassCastException();
    }
    return this.linkedMap.entrySet().stream().map(entry ->
            new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<Object> values() {
    if (this.linkedMap.values().stream().anyMatch(value ->
        !(value instanceof String))) {
      throw new ClassCastException();
    }
    return new ArrayList<>(this.linkedMap.values());
  }
}
