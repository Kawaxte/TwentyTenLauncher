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

import ch.kawaxte.launcher.impl.LinkedProperties;
import ch.kawaxte.launcher.util.LauncherUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class keeping a map of configuration properties with their keys and values. It includes methods
 * for getting and setting property values by index, and for loading and saving the configuration to
 * a properties file.
 *
 * <p>The configuration file is named with a pattern 'launcher_%username%.properties', where
 * %username% is the current user's username. This file is located in the working directory of the
 * launcher.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class LauncherConfig {

  private static final Logger LOGGER;
  private static final Map<String, Object> PROPERTIES_MAP;

  static {
    LOGGER = LoggerFactory.getLogger(LauncherConfig.class);

    PROPERTIES_MAP = new LinkedHashMap<>();
    PROPERTIES_MAP.put("selectedLanguage", "en");
    PROPERTIES_MAP.put("showBetaVersionsSelected", true);
    PROPERTIES_MAP.put("showAlphaVersionsSelected", false);
    PROPERTIES_MAP.put("showInfdevVersionsSelected", false);
    PROPERTIES_MAP.put("selectedVersion", "b1.1_02");
    PROPERTIES_MAP.put("microsoftProfileId", null);
    PROPERTIES_MAP.put("microsoftProfileName", null);
    PROPERTIES_MAP.put("microsoftAccessToken", null);
    PROPERTIES_MAP.put("microsoftAccessTokenExpiresIn", 0L);
    PROPERTIES_MAP.put("microsoftRefreshToken", null);
    PROPERTIES_MAP.put("microsoftClientToken", UUID.randomUUID().toString().replace("-", "")); // 10
    PROPERTIES_MAP.put("mojangUsername", null);
    PROPERTIES_MAP.put("mojangPassword", null);
    PROPERTIES_MAP.put("mojangRememberPasswordChecked", false);
    PROPERTIES_MAP.put("mojangProfileId", null);
    PROPERTIES_MAP.put("mojangProfileName", null);
    PROPERTIES_MAP.put("mojangProfileLegacy", false);
    PROPERTIES_MAP.put("mojangAccessToken", null);
    PROPERTIES_MAP.put("mojangClientToken", UUID.randomUUID().toString().replace("-", "")); // 18
  }

  private LauncherConfig() {}

  /**
   * Returns the path to the configuration file.
   *
   * @return {@code null} if the file cannot be created, otherwise the path to the configuration
   */
  private static Path getFilePath() {
    String userName = System.getProperty("user.name");
    String fileName = String.format("%s_%s.properties", "launcher", userName);

    Path filePath = LauncherUtils.WORKING_DIRECTORY_PATH.resolve(fileName);
    if (!Files.exists(filePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException ioe) {
        LOGGER.error("Cannot create {}", filePath, ioe);
        return null;
      }
    }
    return filePath;
  }

  private static Object[] getKeys() {
    return PROPERTIES_MAP.keySet().toArray();
  }

  /**
   * Returns the value of the property at the specified index.
   *
   * @param index the index of the property in the ordered map
   * @return the value of the property at the given index
   */
  public static Object get(int index) {
    return PROPERTIES_MAP.get(getKeys()[index].toString());
  }

  /**
   * Sets the value of the property at the specified index.
   *
   * @param index the index of the property in the ordered map
   * @param value the new value for the property
   */
  public static void set(int index, Object value) {
    PROPERTIES_MAP.put(getKeys()[index].toString(), value);
  }

  /**
   * Loads the configuration from a properties file. If the file does not exist or is empty, saves
   * the current configuration.
   *
   * @throws NullPointerException if the {@code filePath} is {@code null}
   * @see #saveConfig()
   */
  public static void loadConfig() {
    Path filePath = getFilePath();
    Objects.requireNonNull(filePath, "filePath cannot be null");

    URI filePathUri = filePath.toUri();

    LinkedProperties properties = new LinkedProperties();

    try (InputStream is = Files.newInputStream(filePath)) {
      properties.load(is);
      properties.forEach((key, value) -> PROPERTIES_MAP.put((String) key, value));
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", filePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", filePathUri, ioe);
    } finally {
      if (properties.isEmpty()) {
        saveConfig();
      }
    }
  }

  /**
   * Saves the current configuration to a properties file.
   *
   * @throws NullPointerException if the {@code filePath} is {@code null}
   */
  public static void saveConfig() {
    Path filePath = getFilePath();
    Objects.requireNonNull(filePath, "filePath cannot be null");

    URI filePathUri = filePath.toUri();

    try (OutputStream os = Files.newOutputStream(filePath)) {
      LinkedProperties properties = new LinkedProperties();

      PROPERTIES_MAP
          .keySet()
          .forEach(
              key -> {
                Object value = PROPERTIES_MAP.get(key);
                properties.put(key, Optional.ofNullable(value).map(Object::toString).orElse(""));
              });
      properties.store(os, "TwentyTen Launcher");

      os.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", filePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot save {}", filePathUri, ioe);
    }
  }
}
