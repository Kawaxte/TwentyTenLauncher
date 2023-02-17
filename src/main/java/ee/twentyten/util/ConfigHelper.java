package ee.twentyten.util;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.custom.CustomLinkedProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public final class ConfigHelper {

  private static final String DEFAULT_USERNAME;
  private static final String DEFAULT_PASSWORD;
  private static final boolean DEFAULT_PASSWORD_SAVED;
  private static final String DEFAULT_CLIENT_TOKEN;
  private static final String DEFAULT_ACCESS_TOKEN;
  private static final String DEFAULT_REFRESH_TOKEN;
  private static final String DEFAULT_PROFILE_ID;
  private static final String DEFAULT_PROFILE_NAME;
  private static final boolean DEFAULT_USING_BETA;
  private static final boolean DEFAULT_USING_ALPHA;
  private static final boolean DEFAULT_USING_INFDEV;
  private static final String DEFAULT_SELECTED_VERSION;
  private static final String DEFAULT_SELECTED_LANGUAGE;

  static {
    DEFAULT_USERNAME = "";
    DEFAULT_PASSWORD = "";
    DEFAULT_PASSWORD_SAVED = false;
    DEFAULT_CLIENT_TOKEN = ConfigHelper.generateClientToken();
    DEFAULT_ACCESS_TOKEN = "";
    DEFAULT_REFRESH_TOKEN = "";
    DEFAULT_PROFILE_ID = "";
    DEFAULT_PROFILE_NAME = "";
    DEFAULT_USING_BETA = true;
    DEFAULT_USING_ALPHA = false;
    DEFAULT_USING_INFDEV = false;
    DEFAULT_SELECTED_VERSION = "b1.1_02";
    DEFAULT_SELECTED_LANGUAGE = ConfigHelper.getLanguage();
  }

  private ConfigHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String generateClientToken() {
    File configFile = new File(FileHelper.workingDirectory,
        "twentyten.properties");

    CustomLinkedProperties properties = new CustomLinkedProperties();

    try (InputStream is = Files.newInputStream(configFile.toPath())) {
      properties.load(is);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to load config file", ioe, true);
    }
    return properties.getProperty("client-token",
        UUID.randomUUID().toString().replace("-", ""));
  }

  private static String getLanguage() {
    return System.getProperty("user.language");
  }

  public static void initConfig() {
    LauncherConfig.instance.setClientToken(DEFAULT_CLIENT_TOKEN);
    LauncherConfig.instance.setAccessToken(DEFAULT_ACCESS_TOKEN);
    LauncherConfig.instance.setUsername(DEFAULT_USERNAME);
    LauncherConfig.instance.setPassword(DEFAULT_PASSWORD);
    LauncherConfig.instance.setPasswordSaved(DEFAULT_PASSWORD_SAVED);
    LauncherConfig.instance.setUsingBeta(DEFAULT_USING_BETA);
    LauncherConfig.instance.setUsingAlpha(DEFAULT_USING_ALPHA);
    LauncherConfig.instance.setUsingInfdev(DEFAULT_USING_INFDEV);
    LauncherConfig.instance.setSelectedVersion(DEFAULT_SELECTED_VERSION);
    LauncherConfig.instance.setSelectedLanguage(DEFAULT_SELECTED_LANGUAGE);
    LauncherConfig.instance.saveConfig();
  }
}
