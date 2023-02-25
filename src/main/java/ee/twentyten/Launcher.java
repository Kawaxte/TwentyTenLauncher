package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;

public class Launcher {

  public static void main(String... args) {
    if (LauncherUtils.MIN_MEMORY > LauncherUtils.MAX_MEMORY) {
      LauncherUtils.buildLowMemoryProcess();
    }

    Launcher.init();
    LauncherFrame.main(args);
  }

  private static void init() {
    ConfigUtils.readFromConfig();
    LanguageUtils.loadLocaleFile(ConfigUtils.config.getSelectedLanguage());
  }
}
