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
package io.github.kawaxte.twentyten;

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

  private final Map<String, String> lookupMap;

  public UTF8ResourceBundle() {
    this.lookupMap = new HashMap<>();
  }

  public UTF8ResourceBundle(InputStream is) throws IOException {
    this.lookupMap = new HashMap<>();

    try (val br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      val properties = new Properties();
      properties.load(br);
      properties
          .stringPropertyNames()
          .forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
    }
  }

  public UTF8ResourceBundle(Reader reader) throws IOException {
    this.lookupMap = new HashMap<>();

    try (val br = new BufferedReader(reader)) {
      val properties = new Properties();
      properties.load(br);
      properties
          .stringPropertyNames()
          .forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
    }
  }

  @Override
  protected Object handleGetObject(String key) {
    return this.lookupMap.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    return Collections.enumeration(this.lookupMap.keySet());
  }

  public static class UTF8Control extends Control {

    @Override
    public ResourceBundle newBundle(
        String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
        throws IOException {
      val bundleName = this.toBundleName(baseName, locale);
      val resourceName = this.toResourceName(bundleName, "properties");
      try (val is = loader.getResourceAsStream(resourceName)) {
        return new UTF8ResourceBundle(is);
      }
    }
  }
}
