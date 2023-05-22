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
package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.UTF8ResourceBundle.UTF8Control;
import io.github.kawaxte.twentyten.launcher.util.LauncherOptionsUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherLanguage {

  private static final Logger LOGGER;

  public static UTF8ResourceBundle bundle;

  static {
    LOGGER = LogManager.getLogger(LauncherLanguage.class);

    if (Objects.isNull(bundle)) {
      bundle = new UTF8ResourceBundle();
    } else {
      throw new IllegalStateException("bundle is already initialised");
    }
  }

  private LauncherLanguage() {}

  public static UTF8ResourceBundle getUTF8Bundle(String languageCode) {
    return Optional.ofNullable(languageCode)
        .map(
            code ->
                (UTF8ResourceBundle)
                    UTF8ResourceBundle.getBundle(
                        "messages", Locale.forLanguageTag(code), new UTF8Control()))
        .orElseGet(
            () -> (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages", new UTF8Control()));
  }

  public static void loadLanguage(String baseName, String languageCode) {
    String fileName = String.format("%s_%s.properties", baseName, languageCode);
    URL fileUrl = LauncherOptionsUtils.class.getClassLoader().getResource(fileName);

    InputStream is =
        Optional.ofNullable(
                LauncherOptionsUtils.class.getClassLoader().getResourceAsStream(fileName))
            .orElseThrow(() -> new NullPointerException("is cannot be null"));
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      bundle = new UTF8ResourceBundle(br);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load", fileUrl, ioe);
    } finally {
      LOGGER.info("Loaded {}", fileUrl);
    }
  }
}
