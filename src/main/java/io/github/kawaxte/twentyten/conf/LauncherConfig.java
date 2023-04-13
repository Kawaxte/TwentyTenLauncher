package io.github.kawaxte.twentyten.conf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfig {

  private static final List<String> VERSION_TYPES;
  public static Map<String, String> versionLookup;
  public static URL versionsFileUrl;
  static Logger logger;

  static {
    logger = LogManager.getLogger(LauncherConfig.class);

    try {
      versionsFileUrl = new URL(new StringBuilder()
          .append("https://raw.githubusercontent.com/")
          .append("Kawaxte/")
          .append("TwentyTenLauncher/")
          .append("nightly/")
          .append("versions.json").toString());
    } catch (MalformedURLException murle) {
      logger.error("Failed to create URL for versions file", murle);
    }

    VERSION_TYPES = Collections.unmodifiableList(Arrays.asList("beta", "alpha", "infdev"));
  }

  private LauncherConfig() {
  }

  public static void loadConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.loadConfig();
    } catch (IOException ioe) {
      logger.error("Failed to load config file",
          ioe);
    }
  }

  public static void saveConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.saveConfig();
    } catch (IOException ioe) {
      logger.error("Failed to save config file",
          ioe);
    }
  }
}
