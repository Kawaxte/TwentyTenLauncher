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

import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.workingDirectoryPath;

import io.github.kawaxte.twentyten.LinkedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfig {

  private static final Logger LOGGER;
  public static Map<String, Object> lookup;

  static {
    LOGGER = LogManager.getLogger(LauncherConfig.class);

    lookup = new LinkedHashMap<>();
    lookup.put("selectedLanguage", "en");
    lookup.put("showBetaVersionsSelected", true);
    lookup.put("showAlphaVersionsSelected", false);
    lookup.put("showInfdevVersionsSelected", false);
    lookup.put("selectedVersion", "b1.1_02");
    lookup.put("microsoftProfileId", null);
    lookup.put("microsoftProfileName", null);
    lookup.put("microsoftAccessToken", null);
    lookup.put("microsoftAccessTokenExpiresIn", 0L);
    lookup.put("microsoftRefreshToken", null);
    lookup.put("microsoftClientToken", UUID.randomUUID().toString().replace("-", ""));
    lookup.put("mojangUsername", null);
    lookup.put("mojangPassword", null);
    lookup.put("mojangRememberPasswordChecked", false);
    lookup.put("mojangProfileId", null);
    lookup.put("mojangProfileName", null);
    lookup.put("mojangProfileLegacy", false);
    lookup.put("mojangAccessToken", null);
    lookup.put("mojangClientToken", UUID.randomUUID().toString().replace("-", ""));
  }

  private LauncherConfig() {}

  private static Path getConfigFilePath() {
    String userName = System.getProperty("user.name");
    String fileName = String.format("%s_%s.properties", "twentyten", userName);

    Path filePath = Paths.get(String.valueOf(workingDirectoryPath), fileName);
    File configFile = filePath.toFile();
    try {
      return !configFile.exists() && !configFile.createNewFile() ? null : filePath;
    } catch (IOException ioe) {
      LOGGER.error("Cannot create {}", configFile.getAbsolutePath(), ioe);
    }
    return null;
  }

  public static void loadConfig() {
    Path filePath = getConfigFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI filePathUri = filePath.toUri();

    LinkedProperties properties = new LinkedProperties();
    try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
      properties.load(fis);
      properties.forEach((key, value) -> lookup.put((String) key, value));
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", filePathUri, fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", filePathUri, ioe);
    } finally {
      if (!properties.isEmpty()) {
        LOGGER.info("Loaded {}", filePathUri);
      } else {
        saveConfig();
      }
    }
  }

  public static void saveConfig() {
    Path filePath = getConfigFilePath();
    if (Objects.isNull(filePath)) {
      return;
    }

    URI filePathUri = filePath.toUri();

    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
      LinkedProperties properties = new LinkedProperties();

      lookup
          .keySet()
          .forEach(
              key -> {
                Object value = lookup.get(key);
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
        LOGGER.info("Saved {}", filePathUri);
      }
    }
  }
}
