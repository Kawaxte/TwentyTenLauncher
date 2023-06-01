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

package ch.kawaxte.launcher;

import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle.UTF8Control;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing methods for getting a {@link UTF8ResourceBundle} for a specific language code,
 * and loading language resources from a given base name and language code in ISO 639-1 format.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class LauncherLanguage {

  private static final Logger LOGGER;
  @Getter private static UTF8ResourceBundle bundle;

  static {
    LOGGER = LoggerFactory.getLogger(LauncherLanguage.class);
  }

  private LauncherLanguage() {}

  /**
   * Returns a {@link UTF8ResourceBundle} for the provided language code. If the language code is
   * {@code null}, a {@link UTF8ResourceBundle} for the default locale is returned.
   *
   * @param languageCode the ISO 639-1 language code for which to get the resource bundle
   * @return the {@link UTF8ResourceBundle} for the given language code, or for the default locale
   *     if the language code is {@code null}
   * @see ResourceBundle#getBundle(String, Locale)
   * @see Locale#forLanguageTag(String)
   * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1
   *     codes</a>
   */
  public static UTF8ResourceBundle getUTF8Bundle(String languageCode) {
    String baseName = "assets/lang/messages";
    return Optional.ofNullable(languageCode)
        .map(
            code ->
                (UTF8ResourceBundle)
                    ResourceBundle.getBundle(
                        baseName, Locale.forLanguageTag(code), new UTF8Control()))
        .orElseGet(
            () -> (UTF8ResourceBundle) ResourceBundle.getBundle(baseName, new UTF8Control()));
  }

  /**
   * Loads a language resource file for a given base name and language code. The file is read as
   * UTF-8, and is expected to be a properties file.
   *
   * <p>The resource file is located in the classpath and is named in the format:
   * baseName_languageCode.properties
   *
   * @param baseName the base name of the resource file to load
   * @param languageCode the language code of the resource file to load
   * @throws NullPointerException if the resource file cannot be found
   * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1
   *     codes</a>
   */
  public static void loadLanguage(String baseName, String languageCode) {
    Objects.requireNonNull(baseName, "baseName cannot be null");
    Objects.requireNonNull(languageCode, "languageCode cannot be null");

    String fileName = String.format("%s_%s.properties", baseName, languageCode);
    URL fileUrl =
        LauncherLanguage.class
            .getClassLoader()
            .getResource(String.format("assets/lang/%s", fileName));

    InputStream is =
        Optional.ofNullable(
                LauncherLanguage.class
                    .getClassLoader()
                    .getResourceAsStream(String.format("assets/lang/%s", fileName)))
            .orElseThrow(() -> new NullPointerException("is cannot be null"));
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      bundle = new UTF8ResourceBundle(br);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", fileUrl, ioe);
    }
  }
}
