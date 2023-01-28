package ee.twentyten.utils;

import ee.twentyten.LauncherConfig;
import ee.twentyten.core.LinkedProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class ConfigManager {

  private static final String DEFAULT_CLIENT_TOKEN = ConfigManager.getClientToken();
  private static final String DEFAULT_ACCESS_TOKEN = "";
  private static final String DEFAULT_USERNAME = "";
  private static final String DEFAULT_PASSWORD = "";
  private static final boolean DEFAULT_REMEMBER_PASSWORD = false;
  private static final boolean DEFAULT_BETA_VERSION = true;
  private static final boolean DEFAULT_ALPHA_VERSION = false;
  private static final boolean DEFAULT_INFDEV_VERSION = false;
  private static final String DEFAULT_VERSION_ID = "b1.1_02";

  private ConfigManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getClientToken() {
    File configFile = new File(DirectoryManager.getGameDirectory(), "twentyten.properties");
    if (!configFile.exists()) {
      return UUID.randomUUID().toString().replace("-", "");
    }

    LinkedProperties properties = new LinkedProperties();
    try {
      properties.load(Files.newInputStream(configFile.toPath()));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config file!", e);
    }

    String clientToken = UUID.randomUUID().toString().replace("-", "");
    return properties.getProperty("client-token", clientToken);
  }

  public static void initConfig() {
    try {
      LauncherConfig config = LauncherConfig.class.newInstance();
      config.setClientToken(DEFAULT_CLIENT_TOKEN);
      config.setAccessToken(DEFAULT_ACCESS_TOKEN);
      config.setUsername(DEFAULT_USERNAME);
      config.setPassword(DEFAULT_PASSWORD);
      config.setRememberBox(DEFAULT_REMEMBER_PASSWORD);
      config.setBetaBox(DEFAULT_BETA_VERSION);
      config.setAlphaBox(DEFAULT_ALPHA_VERSION);
      config.setInfdevBox(DEFAULT_INFDEV_VERSION);
      config.setVersionId(DEFAULT_VERSION_ID);
      config.save();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Failed to initialise config", e);
    }
  }
}
