package ee.twentyten.util;

import ee.twentyten.config.Config;
import ee.twentyten.custom.CustomLinkedProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    DEFAULT_CLIENT_TOKEN = getClientToken();
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
    File configFile = new File(LauncherManager.getWorkingDirectory(), "twentyten.properties");
    if (!configFile.exists()) {
      return UUID.randomUUID().toString().replace("-", "");
    }

    CustomLinkedProperties properties = new CustomLinkedProperties();
    try (InputStream is = Files.newInputStream(configFile.toPath())) {
      properties.load(is);
    } catch (IOException ioe1) {
      try (InputStream is = ConfigManager.class.getResourceAsStream("twentyten.properties")) {
        properties.load(is);
      } catch (IOException ioe2) {
        LoggingManager.logError(ConfigManager.class,
            String.format("Failed to load config file from \"%s\"", configFile.getAbsolutePath()),
            ioe2);
      }
    }

    String clientToken = UUID.randomUUID().toString().replace("-", "");
    return properties.getProperty("client-token", clientToken);
  }

  public static void initConfig() {
    Config.instance.setClientToken(DEFAULT_CLIENT_TOKEN);
    Config.instance.setAccessToken(DEFAULT_ACCESS_TOKEN);
    Config.instance.setUsername(DEFAULT_USERNAME);
    Config.instance.setPassword(DEFAULT_PASSWORD);
    Config.instance.setPasswordSaved(DEFAULT_REMEMBER_PASSWORD);
    Config.instance.setUsingBeta(DEFAULT_BETA_VERSION);
    Config.instance.setUsingAlpha(DEFAULT_ALPHA_VERSION);
    Config.instance.setUsingInfdev(DEFAULT_INFDEV_VERSION);
    Config.instance.setSelectedVersion(DEFAULT_VERSION_ID);
    Config.instance.save();
  }
}
