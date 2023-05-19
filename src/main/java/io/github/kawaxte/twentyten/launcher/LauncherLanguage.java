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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.www.protocol.file.FileURLConnection;

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
    val fileName = String.format("%s_%s.properties", baseName, languageCode);
    Optional<URL> fileUrl =
        Optional.ofNullable(LauncherLanguage.class.getClassLoader().getResource(fileName));
    int fileUrlIndex =
        fileUrl
            .map(value -> value.getFile().lastIndexOf("/"))
            .orElseThrow(() -> new NullPointerException("fileUrl cannot be null"));
    fileUrl.ifPresent(
        url -> {
          FileURLConnection connection = null;
          try {
            connection = (FileURLConnection) url.openConnection();
            try (InputStream is = url.openConnection().getInputStream();
                val isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
              bundle = new UTF8ResourceBundle(isr);
            }
          } catch (IOException ioe) {
            LOGGER.error(
                "Cannot load {} from {}",
                fileUrl.get().getFile().substring(fileUrlIndex + 1),
                fileUrl.get().getFile().substring(0, fileUrlIndex),
                ioe);
          } finally {
            if (Objects.nonNull(connection)) {
              connection.close();
            }

            LOGGER.info(
                "Loaded {} from {}",
                fileUrl.get().getFile().substring(fileUrlIndex + 1),
                fileUrl.get().getFile().substring(0, fileUrlIndex));
          }
        });
  }
}
