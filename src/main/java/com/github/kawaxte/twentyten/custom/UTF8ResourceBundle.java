package com.github.kawaxte.twentyten.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class UTF8ResourceBundle extends ResourceBundle {

  private final Map<String, String> lookup;

  public UTF8ResourceBundle() {
    this.lookup = new HashMap<>();
  }

  public UTF8ResourceBundle(InputStream is) throws IOException {
    this.lookup = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      Properties props = new Properties();
      props.load(br);
      props.stringPropertyNames().forEach(key -> this.lookup.put(key, props.getProperty(key)));
    }
  }

  public UTF8ResourceBundle(Reader reader) throws IOException {
    this.lookup = new HashMap<>();
    try (BufferedReader br = new BufferedReader(reader)) {
      Properties props = new Properties();
      props.load(br);
      props.stringPropertyNames().forEach(key -> this.lookup.put(key, props.getProperty(key)));
    }
  }

  @Override
  protected Object handleGetObject(String key) {
    return this.lookup.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    return Collections.enumeration(this.lookup.keySet());
  }

  public static class UTF8Control extends Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
        boolean reload) throws IOException {
      String bundleName = this.toBundleName(baseName, locale);
      String resourceName = this.toResourceName(bundleName, "properties");
      try (InputStream is = loader.getResourceAsStream(resourceName)) {
        return new UTF8ResourceBundle(is);
      }
    }
  }
}
