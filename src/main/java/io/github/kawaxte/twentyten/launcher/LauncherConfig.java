package io.github.kawaxte.twentyten.launcher;

import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.workingDirectoryPath;

import io.github.kawaxte.twentyten.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfig {

  private static final Logger LOGGER;
  public static Map<String, Object> lookup;

  static {
    LOGGER = LogManager.getLogger(LauncherConfig.class);

    lookup = new HashMap<>();
    lookup.put("selectedLanguage", "en");
    lookup.put("showBetaVersionsSelected", true);
    lookup.put("showAlphaVersionsSelected", false);
    lookup.put("showInfdevVersionsSelected", false);
    lookup.put("selectedVersion", "b1.1_02");
    lookup.put("microsoftProfileDemo", false);
    lookup.put("microsoftProfileId", null);
    lookup.put("microsoftProfileName", null);
    lookup.put("microsoftAccessToken", null);
    lookup.put("microsoftAccessTokenExpiresIn", 0L);
    lookup.put("microsoftRefreshToken", null);
    lookup.put("microsoftClientToken", null);
    lookup.put("mojangUsername", null);
    lookup.put("mojangPassword", null);
    lookup.put("mojangRememberPasswordChecked", false);
    lookup.put("mojangProfileDemo", false);
    lookup.put("mojangProfileId", null);
    lookup.put("mojangProfileName", null);
    lookup.put("mojangProfileLegacy", false);
    lookup.put("mojangAccessToken", null);
    lookup.put("mojangClientToken", null);
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
      LOGGER.error("Failed to create {}", configFile.getAbsolutePath(), ioe);
    }
    return null;
  }

  public static void loadConfig() {
    val configFilePath = getConfigFilePath();
    if (configFilePath == null) {
      return;
    }

    val properties = new LinkedProperties();
    try (val stream = new FileInputStream(configFilePath.toFile())) {
      properties.load(stream);
      properties.forEach((key, value) -> lookup.put((String) key, value));
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to find {}", configFilePath.toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Failed to load {}", configFilePath.toAbsolutePath(), ioe);
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
    if (configFilePath == null) {
      return;
    }

    try (val stream = new FileOutputStream(configFilePath.toFile())) {
      val properties = new LinkedProperties();

      lookup.forEach(
          (key, value) -> properties.put(key, Objects.isNull(value) ? "" : value.toString()));
      properties.store(stream, "TwentyTen Launcher");

      stream.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to locate {}", configFilePath.toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Failed to save {}", configFilePath.toAbsolutePath(), ioe);
    } finally {
      LOGGER.info(
          "Saved {} to {}",
          configFilePath.getFileName(),
          configFilePath.toAbsolutePath().getParent());
    }
  }
}
