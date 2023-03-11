package ee.twentyten.custom;

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
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

public class UTF8ResourceBundle extends ResourceBundle {

  private final Map<String, String> customLookup;

  public UTF8ResourceBundle(InputStream is) throws IOException {
    this.customLookup = new HashMap<>();
    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr)) {
      Properties props = new Properties();
      props.load(br);
      for (String key : props.stringPropertyNames()) {
        this.customLookup.put(key, props.getProperty(key));
      }
    }
  }

  public UTF8ResourceBundle(Reader reader) throws IOException {
    this.customLookup = new HashMap<>();
    try (BufferedReader br = new BufferedReader(reader)) {
      Properties props = new Properties();
      props.load(br);
      for (String key : props.stringPropertyNames()) {
        this.customLookup.put(key, props.getProperty(key));
      }
    }
  }

  public static UTF8ResourceBundle getCustomBundle(String baseName) {
    ResourceBundle.Control control = new ResourceBundle.Control() {
      @Override
      public ResourceBundle newBundle(String baseName, Locale locale, String format,
          ClassLoader loader, boolean reload) throws IOException {
        String bundleName = this.toBundleName(baseName, locale);
        String resourceName = this.toResourceName(bundleName, "properties");
        try (InputStream is = loader.getResourceAsStream(resourceName)) {
          Objects.requireNonNull(is, "is == null!");
          return new UTF8ResourceBundle(is);
        }
      }
    };
    return (UTF8ResourceBundle) ResourceBundle.getBundle(baseName, control);
  }

  @Override
  protected Object handleGetObject(String key) {
    return this.customLookup.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    return Collections.enumeration(this.customLookup.keySet());
  }
}
