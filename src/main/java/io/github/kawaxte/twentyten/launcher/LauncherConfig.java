package io.github.kawaxte.twentyten.launcher;

import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.workingDirectoryPath;

import io.github.kawaxte.twentyten.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.val;
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
    val userName = System.getProperty("user.name");
    val fileName = String.format("%s_%s.properties", "twentyten", userName);
    val filePath = Paths.get(String.valueOf(workingDirectoryPath), fileName);
    val configFile = filePath.toFile();
    try {
      return !configFile.exists() && !configFile.createNewFile() ? null : filePath;
    } catch (IOException ioe) {
      LOGGER.error("Cannot create {}", configFile.getAbsolutePath(), ioe);
    }
    return null;
  }

  public static void loadConfig() {
    val configFilePath = getConfigFilePath();
    if (Objects.isNull(configFilePath)) {
      return;
    }

    val properties = new LinkedProperties();
    try (val fis = new FileInputStream(configFilePath.toFile())) {
      properties.load(fis);
      properties.forEach((key, value) -> lookup.put((String) key, value));
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", configFilePath.toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot load {}", configFilePath.toAbsolutePath(), ioe);
    } finally {
      if (!properties.isEmpty()) {
        LOGGER.info(
            "Loaded {} from {}",
            configFilePath.getFileName(),
            configFilePath.toAbsolutePath().getParent());
      } else {
        saveConfig();
      }
    }
  }

  public static void saveConfig() {
    val configFilePath = getConfigFilePath();
    if (Objects.isNull(configFilePath)) {
      return;
    }

    try (val fos = new FileOutputStream(configFilePath.toFile())) {
      val properties = new LinkedProperties();

      lookup
          .keySet()
          .forEach(
              key -> {
                val value = lookup.get(key);
                properties.put(key, Optional.ofNullable(value).map(Object::toString).orElse(""));
              });
      properties.store(fos, "TwentyTen Launcher");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot find {}", configFilePath.toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Cannot save {}", configFilePath.toAbsolutePath(), ioe);
    } finally {
      LOGGER.info(
          "Saved {} to {}",
          configFilePath.getFileName(),
          configFilePath.toAbsolutePath().getParent());
    }
  }
}
