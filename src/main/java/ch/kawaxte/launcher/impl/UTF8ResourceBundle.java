/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.kawaxte.launcher.impl;

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
import javax.annotation.Nonnull;

/**
 * Class extending {@link java.util.ResourceBundle} and provides the functionality to read and store
 * properties from UTF-8 encoded properties files. It uses a HashMap as an underlying data structure
 * to store properties, which makes the lookup operations efficient.
 *
 * <p>The associated inner class {@code UTF8Control} extends {@link
 * java.util.ResourceBundle.Control} and is used to create a new UTF8ResourceBundle when requested
 * by the ResourceBundle factory methods.
 *
 * @see java.util.ResourceBundle
 * @see java.util.ResourceBundle.Control
 * @author Kawaxte
 * @since 1.3.3023_04
 */
public class UTF8ResourceBundle extends ResourceBundle {

  private final Map<String, String> lookupMap;

  public UTF8ResourceBundle() {
    this.lookupMap = new HashMap<>();
  }

  public UTF8ResourceBundle(InputStream is) throws IOException {
    this.lookupMap = new HashMap<>();

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      Properties properties = new Properties();
      properties.load(br);
      properties
          .stringPropertyNames()
          .forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
    }
  }

  public UTF8ResourceBundle(Reader reader) throws IOException {
    this.lookupMap = new HashMap<>();

    try (BufferedReader br = new BufferedReader(reader)) {
      Properties properties = new Properties();
      properties.load(br);
      properties
          .stringPropertyNames()
          .forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
    }
  }

  @Override
  protected Object handleGetObject(@Nonnull String key) {
    return this.lookupMap.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    return Collections.enumeration(this.lookupMap.keySet());
  }

  /**
   * A custom ResourceBundle.Control implementation for creating UTF8ResourceBundle.
   *
   * <p>This class overrides {@link #newBundle(String, Locale, String, ClassLoader, boolean)} to
   * create a UTF8ResourceBundle when requested by the ResourceBundle factory methods.
   *
   * @see java.util.ResourceBundle.Control
   * @see java.util.ResourceBundle.Control#newBundle(String, Locale, String, ClassLoader, boolean)
   */
  public static class UTF8Control extends Control {

    @Override
    public ResourceBundle newBundle(
        String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
        throws IOException {
      String bundleName = this.toBundleName(baseName, locale);
      String resourceName = this.toResourceName(bundleName, "properties");

      try (InputStream is = loader.getResourceAsStream(resourceName)) {
        return new UTF8ResourceBundle(is);
      }
    }
  }
}
