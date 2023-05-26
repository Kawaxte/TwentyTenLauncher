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
import java.util.ResourceBundle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This final class is responsible for managing the launcher's language settings. The language
 * settings are stored in a resource bundle file named "{baseName}_{languageCode}.properties", where
 * the baseName is "messages" and the languageCode is a two-letter ISO 639-1 language code.
 *
 * <p>When the launcher starts, the appropriate language file is loaded into a UTF8ResourceBundle,
 * which allows the launcher to display messages in the user's selected language. If no language
 * code is provided, the default language file (i.e., "messages.properties") is loaded.
 *
 * <p>This class also provides a static method for retrieving the current language bundle, which can
 * be used by other classes to access localized messages.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class LauncherLanguage {

  private static final Logger LOGGER;
  @Getter private static UTF8ResourceBundle bundle;

  static {
    LOGGER = LogManager.getLogger(LauncherLanguage.class);
  }

  private LauncherLanguage() {}

  /**
   * Method to return a UTF8ResourceBundle for the provided language code.
   *
   * @param languageCode The two-letter ISO 639-1 code of the desired language.
   * @return A default UTF8ResourceBundle if the language code is {@code null} or empty, otherwise
   *     the UTF8ResourceBundle for the provided language code.
   * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1
   *     codes</a>
   * @see ResourceBundle#getBundle(String, Locale, ResourceBundle.Control)
   * @see Locale#forLanguageTag(String)
   */
  public static UTF8ResourceBundle getUTF8Bundle(String languageCode) {
    return Optional.ofNullable(languageCode)
        .map(
            code ->
                (UTF8ResourceBundle)
                    ResourceBundle.getBundle(
                        "messages", Locale.forLanguageTag(code), new UTF8Control()))
        .orElseGet(
            () -> (UTF8ResourceBundle) ResourceBundle.getBundle("messages", new UTF8Control()));
  }

  /**
   * Method to load a language file into the UTF8ResourceBundle. If the file cannot be loaded, an
   * error is logged and the application continues to run.
   *
   * <p>Example: {@code LauncherLanguage.loadLanguage("messages", "en");} will load the
   * "messages_en.properties" file, which contains the English language messages.
   *
   * @param baseName The base name of the language file (without the language code or file
   *     extension).
   * @param languageCode The two-letter ISO 639-1 code of the desired language.
   * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1
   *     codes</a>
   */
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
      LOGGER.error("Cannot load {}", fileUrl, ioe);
    } finally {
      if (Objects.nonNull(fileUrl) && Objects.nonNull(bundle)) {
        LOGGER.info("Loading {}", fileUrl);
      }
    }
  }
}
