package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.launcher.LauncherUtils;
import ee.twentyten.util.launcher.options.LanguageUtils;
import ee.twentyten.util.launcher.options.VersionUtils;

public class Launcher {

  public static void main(String... args) {
    if (LauncherUtils.MIN_MEMORY > LauncherUtils.MAX_MEMORY) {
      LauncherUtils.buildAndCreateProcess();
    }

    ConfigUtils.readFromConfig();
    LanguageUtils.loadLocale();
    VersionUtils.getVersionsFile();

    LauncherFrame.main(args);
  }
}
