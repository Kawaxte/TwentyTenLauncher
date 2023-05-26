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

import io.github.kawaxte.twentyten.LinkedProperties;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This final class is responsible for managing the launcher's configuration properties. The
 * configuration properties are stored in a properties file named "twentyten_{username}.properties"
 * in the working directory, where the username is the current system user's name.
 *
 * <p>The configuration is loaded from the properties file when the launcher starts and is saved
 * back to the properties file whenever a configuration property changes.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class LauncherConfig {

  private static final Logger LOGGER;
  private static final Map<String, Object> PROPERTIES_MAP;

  static {
    LOGGER = LogManager.getLogger(LauncherConfig.class);

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
   * Method to return the path to the properties file where the launcher's configuration is stored.
   *
   * <p>The path is determined by the following steps:
   *
   * <ol>
   *   <li>Get the current system user's name.
   *   <li>Format the user name and launcher name into a file name.
   *   <li>Resolve the file name against the working directory.
   *   <li>Get the file from the path.
   *   <li>If the file does not exist, create it.
   *   <li>Return the path to the file.
   * </ol>
   *
   * @return {@code null} if the file cannot be created, otherwise the Path to the file.
   * @see Path#resolve(String)
   */
  private static Path getFilePath() {
    String userName = System.getProperty("user.name");
    String fileName = String.format("%s_%s.properties", "twentyten", userName);

    Path filePath = LauncherUtils.WORKING_DIRECTORY_PATH.resolve(fileName);
    File file = filePath.toFile();
    try {
      return !file.exists() && !file.createNewFile() ? null : filePath;
    } catch (IOException ioe) {
      LOGGER.error("Cannot create {}", filePath, ioe);
    }
    return null;
  }

  /**
   * Method to retrieve an array of keys for the properties map. The keys are retrieved from the
   * properties map and stored in an Object array.
   *
   * <p>Each property is accessed via an index which corresponds to the order of the keys in the
   * properties map. The initial configuration properties and their corresponding indexes are as
   * follows:
   *
   * <ul>
   *   <li>0: selectedLanguage (default: "en")
   *   <li>1: showBetaVersionsSelected (default: true)
   *   <li>2: showAlphaVersionsSelected (default: false)
   *   <li>3: showInfdevVersionsSelected (default: false)
   *   <li>4: selectedVersion (default: "b1.1_02")
   *   <li>5: microsoftProfileId (default: null)
   *   <li>6: microsoftProfileName (default: null)
   *   <li>7: microsoftAccessToken (default: null)
   *   <li>8: microsoftAccessTokenExpiresIn (default: 0L)
   *   <li>9: microsoftRefreshToken (default: null)
   *   <li>10: microsoftClientToken (default: randomly generated UUID without dashes)
   *   <li>11: mojangUsername (default: null)
   *   <li>12: mojangPassword (default: null)
   *   <li>13: mojangRememberPasswordChecked (default: false)
   *   <li>14: mojangProfileId (default: null)
   *   <li>15: mojangProfileName (default: null)
   *   <li>16: mojangProfileLegacy (default: false)
   *   <li>17: mojangAccessToken (default: null)
   *   <li>18: mojangClientToken (default: randomly generated UUID without dashes)
   * </ul>
   *
   * @return An Object array containing the keys for the properties map.
   * @see Map#keySet()
   * @see Set#toArray()
   */
  private static Object[] getKeys() {
    return PROPERTIES_MAP.keySet().toArray();
  }

  /**
   * Method to get a property value from the properties map by its index.
   *
   * @param index The index of the property to retrieve.
   * @return The value of the property, or null if the property does not exist.
   * @see Map#get(Object)
   */
  public static Object get(int index) {
    return PROPERTIES_MAP.get(getKeys()[index].toString());
  }

  /**
   * Method to set a property value in the properties map by its index.
   *
   * @param index The index of the property to set.
   * @param value The value to set for the property.
   * @see Map#put(Object, Object)
   */
  public static void set(int index, Object value) {
    PROPERTIES_MAP.put(getKeys()[index].toString(), value);
  }

  /**
   * Method to load the launcher's configuration from the properties file. If the properties file
   * does not exist, this method will create it and save the initial configuration to it.
   *
   * @see io.github.kawaxte.twentyten.LinkedProperties
   */
  public static void loadConfig() {
    Path filePath = getFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI configFilePathUri = filePath.toUri();

    LinkedProperties properties = new LinkedProperties();

    try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
      properties.load(fis);
      properties.forEach((key, value) -> PROPERTIES_MAP.put((String) key, value));
    } catch (FileNotFoundException fileNotFoundException) {
      LOGGER.error("Cannot find {}", configFilePathUri, fileNotFoundException);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", configFilePathUri, ioe);
    } finally {
      if (!properties.isEmpty()) {
        LOGGER.info("Loading {}", configFilePathUri);
      } else {
        saveConfig();
      }
    }
  }

  /**
   * Method to save the launcher's configuration to the properties file.
   *
   * @see io.github.kawaxte.twentyten.LinkedProperties
   */
  public static void saveConfig() {
    Path filePath = getFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI configFilePathUri = filePath.toUri();

    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
      LinkedProperties properties = new LinkedProperties();

      PROPERTIES_MAP
          .keySet()
          .forEach(
              key -> {
                Object value = PROPERTIES_MAP.get(key);
                properties.put(key, Optional.ofNullable(value).map(Object::toString).orElse(""));
              });
      properties.store(fos, "TwentyTen Launcher");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", configFilePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot save {}", configFilePathUri, ioe);
    } finally {
      if (filePath.toFile().exists()) {
        LOGGER.info("Saving {}", configFilePathUri);
      }
    }
  }
}
