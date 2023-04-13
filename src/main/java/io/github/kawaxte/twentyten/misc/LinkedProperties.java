package io.github.kawaxte.twentyten.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private final Map<Object, Object> linkedMap;

  public LinkedProperties() {
    super();
    this.linkedMap = new LinkedHashMap<>();
  }

  public LinkedProperties(Properties defaults) {
    super(defaults);
    this.linkedMap = new LinkedHashMap<>();
  }

  @Override
  public synchronized Object setProperty(String key, String value) {
    super.setProperty(key, value);
    return this.linkedMap.put(key, value);
  }

  @Override
  public void store(Writer writer, String comments) throws IOException {
    if (comments != null) {
      val sb = new StringBuilder();
      sb.append("#");
      sb.append(comments);
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
  }

  @Override
  public void store(OutputStream os, String comments) throws IOException {
    if (comments != null) {
      val sb = new StringBuilder();
      sb.append("#");
      sb.append(comments);
      sb.append(System.lineSeparator());
      sb.append("#");
      sb.append(new Date());
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
  }

  @Override
  public String getProperty(String key) {
    val value = (String) this.linkedMap.get(key);
    return value == null
        ? super.getProperty(key)
        : value;
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    val value = (String) this.linkedMap.get(key);
    return value == null
        ? defaultValue
        : value;
  }

  @Override
  public synchronized int size() {
    return this.linkedMap.size();
  }

  @Override
  public synchronized boolean isEmpty() {
    return this.linkedMap.isEmpty();
  }

  @Override
  public synchronized Enumeration<Object> keys() {
    Set<Object> keys = this.linkedMap.keySet();
    return Collections.enumeration(keys);
  }

  @Override
  public synchronized Enumeration<Object> elements() {
    Collection<Object> elements = this.linkedMap.values();
    return Collections.enumeration(Collections.singleton(elements));
  }

  @Override
  public synchronized boolean contains(Object value) {
    return this.linkedMap.containsValue(value);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.linkedMap.containsValue(value);
  }

  @Override
  public synchronized boolean containsKey(Object key) {
    return this.linkedMap.containsKey(key);
  }

  @Override
  public synchronized Object get(Object key) {
    return this.linkedMap.get(key);
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    super.put(key, value);
    return this.linkedMap.put(key, value);
  }

  @Override
  public synchronized Object remove(Object key) {
    super.remove(key);
    return this.linkedMap.remove(key);
  }

  @Override
  public synchronized void putAll(Map<?, ?> t) {
    super.putAll(t);
    this.linkedMap.putAll(t);
  }

  @Override
  public synchronized void clear() {
    super.clear();
    this.linkedMap.clear();
  }

  @Override
  public synchronized Object clone() {
    val clone = (LinkedProperties) super.clone();
    clone.linkedMap.putAll(this.linkedMap);
    return clone;
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
  public Collection<Object> values() {
    return this.linkedMap.values();
  }

  @Override
  public synchronized Object getOrDefault(Object key, Object defaultValue) {
    return this.linkedMap.getOrDefault(key, defaultValue);
  }

  @Override
  public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
    this.linkedMap.forEach(action);
  }

  @Override
  public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
    this.linkedMap.replaceAll(function);
  }

  @Override
  public synchronized Object putIfAbsent(Object key, Object value) {
    return this.linkedMap.putIfAbsent(key, value);
  }

  @Override
  public synchronized boolean remove(Object key, Object value) {
    return this.linkedMap.remove(key, value);
  }

  @Override
  public synchronized boolean replace(Object key, Object oldValue, Object newValue) {
    return this.linkedMap.replace(key, oldValue, newValue);
  }

  @Override
  public synchronized Object replace(Object key, Object value) {
    return this.linkedMap.replace(key, value);
  }

  @Override
  public synchronized Object computeIfAbsent(Object key,
      Function<? super Object, ?> mappingFunction) {
    return this.linkedMap.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public synchronized Object computeIfPresent(Object key,
      BiFunction<? super Object, ? super Object, ?> remappingFunction) {
    return this.linkedMap.computeIfPresent(key, remappingFunction);
  }

  @Override
  public synchronized Object compute(Object key,
      BiFunction<? super Object, ? super Object, ?> remappingFunction) {
    return this.linkedMap.compute(key, remappingFunction);
  }

  @Override
  public synchronized Object merge(Object key, Object value,
      BiFunction<? super Object, ? super Object, ?> remappingFunction) {
    return this.linkedMap.merge(key, value, remappingFunction);
  }
}
