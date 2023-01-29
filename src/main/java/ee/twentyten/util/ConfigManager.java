package ee.twentyten.util;

import ee.twentyten.LauncherConfig;
import ee.twentyten.core.LinkedProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class ConfigManager {

  private static final String DEFAULT_CLIENT_TOKEN;
  private static final String DEFAULT_ACCESS_TOKEN;
  private static final String DEFAULT_USERNAME;
  private static final String DEFAULT_PASSWORD;
  private static final boolean DEFAULT_REMEMBER_PASSWORD;
  private static final boolean DEFAULT_BETA_VERSION;
  private static final boolean DEFAULT_ALPHA_VERSION;
  private static final boolean DEFAULT_INFDEV_VERSION;
  private static final String DEFAULT_VERSION_ID;

  static {
    DEFAULT_CLIENT_TOKEN = ConfigManager.getClientToken();
    DEFAULT_ACCESS_TOKEN = "";
    DEFAULT_USERNAME = "";
    DEFAULT_PASSWORD = "";
    DEFAULT_REMEMBER_PASSWORD = false;
    DEFAULT_BETA_VERSION = true;
    DEFAULT_ALPHA_VERSION = false;
    DEFAULT_INFDEV_VERSION = false;
    DEFAULT_VERSION_ID = "b1.1_02";
  }

  private ConfigManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getClientToken() {
    File configFile = new File(DirectoryManager.getWorkingDirectory(), "twentyten.properties");
    if (!configFile.exists()) {
      return UUID.randomUUID().toString().replace("-", "");
    }

    LinkedProperties properties = new LinkedProperties();
    try {
      properties.load(Files.newInputStream(configFile.toPath()));
    } catch (IOException ioe1) {
      try {
        properties.load(ConfigManager.class.getResourceAsStream("twentyten.properties"));
      } catch (IOException ioe2) {
        throw new RuntimeException(
            String.format("Failed to load config file from %s", configFile.getAbsolutePath()),
            ioe2);
      }
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
    } catch (InstantiationException | IllegalAccessException exceptions) {
      throw new RuntimeException("Failed to initialise LauncherConfig", exceptions);
    }
  }
}
