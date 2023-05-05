package io.github.kawaxte.twentyten.conf;

import java.io.IOException;
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
  static Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(LauncherConfig.class);

    try {
      versionsFileUrl =
          new URL(
              new StringBuilder()
                  .append("https://raw.githubusercontent.com/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("nightly/")
                  .append("versions.json")
                  .toString());
    } catch (IOException ioe) {
      LOGGER.error("Failed to create URL for {}", versionsFileUrl.toString(), ioe);
    }

    VERSION_TYPES = Collections.unmodifiableList(Arrays.asList("beta", "alpha", "infdev"));
  }

  private LauncherConfig() {}

  public static void loadConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.load();
    } catch (IOException ioe) {
      LOGGER.error("Failed to load config file", ioe);
    }
  }

  public static void saveConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.save();
    } catch (IOException ioe) {
      LOGGER.error("Failed to save config file", ioe);
    }
  }
}
