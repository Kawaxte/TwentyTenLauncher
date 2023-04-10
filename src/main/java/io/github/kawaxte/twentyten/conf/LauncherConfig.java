package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

public final class LauncherConfig {

  private static final List<String> VERSION_TYPES;
  public static Map<String, String> versionLookup;
  public static URL versionsFileUrl;

  static {
    LauncherUtils.logger = LogManager.getLogger(LauncherConfig.class);

    try {
      versionsFileUrl = new URL("https://raw.githubusercontent.com/"
          + "Kawaxte/"
          + "TwentyTenLauncher/"
          + "nightly/"
          + "versions.json");
    } catch (MalformedURLException murle) {
      LauncherUtils.logger.error("Failed to create URL for versions file", murle);
    }

    VERSION_TYPES = Collections.unmodifiableList(Arrays.asList(
        "beta",
        "alpha",
        "infdev"));
  }

  private LauncherConfig() {
  }

  public static void loadConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.loadConfig();
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to load config file",
          ioe);
    }
  }

  public static void saveConfig() {
    try {
      AbstractLauncherConfigImpl.INSTANCE.saveConfig();
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to save config file",
          ioe);
    }
  }
}
