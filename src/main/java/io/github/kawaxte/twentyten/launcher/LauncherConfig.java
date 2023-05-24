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
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfig {

  private static final Logger LOGGER;
  private static final Map<String, Object> PROPERTIES_MAP;

  static {
    LOGGER = LogManager.getLogger(LauncherConfig.class);

    PROPERTIES_MAP = new LinkedHashMap<>();
    PROPERTIES_MAP.put("selectedLanguage", "en"); // 0
    PROPERTIES_MAP.put("showBetaVersionsSelected", true); // 1
    PROPERTIES_MAP.put("showAlphaVersionsSelected", false); // 2
    PROPERTIES_MAP.put("showInfdevVersionsSelected", false); // 3
    PROPERTIES_MAP.put("selectedVersion", "b1.1_02"); // 4
    PROPERTIES_MAP.put("microsoftProfileId", null); // 5
    PROPERTIES_MAP.put("microsoftProfileName", null); // 6
    PROPERTIES_MAP.put("microsoftAccessToken", null); // 7
    PROPERTIES_MAP.put("microsoftAccessTokenExpiresIn", 0L); // 8
    PROPERTIES_MAP.put("microsoftRefreshToken", null); // 9
    PROPERTIES_MAP.put("microsoftClientToken", UUID.randomUUID().toString().replace("-", "")); // 10
    PROPERTIES_MAP.put("mojangUsername", null); // 11
    PROPERTIES_MAP.put("mojangPassword", null); // 12
    PROPERTIES_MAP.put("mojangRememberPasswordChecked", false); // 13
    PROPERTIES_MAP.put("mojangProfileId", null); // 14
    PROPERTIES_MAP.put("mojangProfileName", null); // 15
    PROPERTIES_MAP.put("mojangProfileLegacy", false); // 16
    PROPERTIES_MAP.put("mojangAccessToken", null); // 17
    PROPERTIES_MAP.put("mojangClientToken", UUID.randomUUID().toString().replace("-", "")); // 18
  }

  private LauncherConfig() {}

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

  private static Object[] getKeys() {
    return PROPERTIES_MAP.keySet().toArray();
  }

  public static Object get(int index) {
    return PROPERTIES_MAP.get(getKeys()[index].toString());
  }

  public static void set(int index, Object value) {
    PROPERTIES_MAP.put(getKeys()[index].toString(), value);
  }

  public static void loadConfig() {
    Path filePath = getFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI filePathUri = filePath.toUri();

    LinkedProperties properties = new LinkedProperties();

    try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
      properties.load(fis);
      properties.forEach((key, value) -> PROPERTIES_MAP.put((String) key, value));
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", filePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", filePathUri, ioe);
    } finally {
      if (!properties.isEmpty()) {
        LOGGER.info("Loading {}", filePathUri);
      } else {
        saveConfig();
      }
    }
  }

  public static void saveConfig() {
    Path filePath = getFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI filePathUri = filePath.toUri();

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
      LOGGER.error("Cannot find {}", filePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot save {}", filePathUri, ioe);
    } finally {
      if (filePath.toFile().exists()) {
        LOGGER.info("Saving {}", filePathUri);
      }
    }
  }
}
