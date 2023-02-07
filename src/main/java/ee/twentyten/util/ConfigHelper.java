package ee.twentyten.util;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.custom.CustomLinkedProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public final class ConfigHelper {

  private static final String DEFAULT_CLIENT_TOKEN;
  private static final String DEFAULT_ACCESS_TOKEN;
  private static final String DEFAULT_USERNAME;
  private static final String DEFAULT_PASSWORD;
  private static final boolean DEFAULT_REMEMBER_PASSWORD;
  private static final boolean DEFAULT_BETA_VERSION;
  private static final boolean DEFAULT_ALPHA_VERSION;
  private static final boolean DEFAULT_INFDEV_VERSION;
  private static final String DEFAULT_VERSION_ID;
  private static final Class<ConfigHelper> CLASS_REF;

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

    CLASS_REF = ConfigHelper.class;
  }

  private ConfigHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getClientToken() {
    File configFile = new File(FileHelper.workingDirectory, "twentyten.properties");
    if (!configFile.exists()) {
      return UUID.randomUUID().toString().replace("-", "");
    }

    CustomLinkedProperties properties = new CustomLinkedProperties();
    try (InputStream is = Files.newInputStream(configFile.toPath())) {
      properties.load(is);
    } catch (IOException ioe1) {
      try (InputStream is = ConfigHelper.class.getResourceAsStream("twentyten.properties")) {
        properties.load(is);
      } catch (IOException ioe2) {
        LogHelper.logError(CLASS_REF,
            String.format("Failed to load config file from \"%s\"", configFile.getAbsolutePath()),
            ioe2);
      }
    }

    String clientToken = UUID.randomUUID().toString().replace("-", "");
    return properties.getProperty("client-token", clientToken);
  }

  public static void initConfig() {
    LauncherConfig.instance.setClientToken(DEFAULT_CLIENT_TOKEN);
    LauncherConfig.instance.setAccessToken(DEFAULT_ACCESS_TOKEN);
    LauncherConfig.instance.setUsername(DEFAULT_USERNAME);
    LauncherConfig.instance.setPassword(DEFAULT_PASSWORD);
    LauncherConfig.instance.setPasswordSaved(DEFAULT_REMEMBER_PASSWORD);
    LauncherConfig.instance.setUsingBeta(DEFAULT_BETA_VERSION);
    LauncherConfig.instance.setUsingAlpha(DEFAULT_ALPHA_VERSION);
    LauncherConfig.instance.setUsingInfdev(DEFAULT_INFDEV_VERSION);
    LauncherConfig.instance.setSelectedVersion(DEFAULT_VERSION_ID);
    LauncherConfig.instance.save();
  }
}
