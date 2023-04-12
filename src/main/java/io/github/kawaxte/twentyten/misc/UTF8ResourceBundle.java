package io.github.kawaxte.twentyten.misc;

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
import lombok.val;

public class UTF8ResourceBundle extends ResourceBundle {

  private final Map<String, String> utf8Lookup;

  public UTF8ResourceBundle() {
    this.utf8Lookup = new HashMap<>();
  }

  public UTF8ResourceBundle(InputStream is) throws IOException {
    this.utf8Lookup = new HashMap<>();

    try (val br = new BufferedReader(new InputStreamReader(is,
        StandardCharsets.UTF_8))) {
      val properties = new Properties();
      properties.load(br);
      properties.stringPropertyNames().forEach(key -> this.utf8Lookup.put(key,
          properties.getProperty(key)));
    }
  }

  public UTF8ResourceBundle(Reader reader) throws IOException {
    this.utf8Lookup = new HashMap<>();

    try (val br = new BufferedReader(reader)) {
      val properties = new Properties();
      properties.load(br);
      properties.stringPropertyNames().forEach(key -> this.utf8Lookup.put(key,
          properties.getProperty(key)));
    }
  }

  @Override
  protected Object handleGetObject(String key) {
    return this.utf8Lookup.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    return Collections.enumeration(this.utf8Lookup.keySet());
  }

  public static class UTF8Control extends Control {

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
        ClassLoader loader, boolean reload) throws IOException {
      val bundleName = this.toBundleName(baseName, locale);
      val resourceName = this.toResourceName(bundleName, "properties");
      try (val is = loader.getResourceAsStream(resourceName)) {
        return new UTF8ResourceBundle(is);
      }
    }
  }
}
