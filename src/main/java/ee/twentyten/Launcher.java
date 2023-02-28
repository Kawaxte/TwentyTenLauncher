package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.VersionUtils;

public class Launcher {

  public static void main(String... args) {
    if (LauncherUtils.MIN_MEMORY > LauncherUtils.MAX_MEMORY) {
      LauncherUtils.buildLowMemoryProcess();
    }

    ConfigUtils.loadConfig();
    LanguageUtils.loadLocale();
    VersionUtils.getVersionsFile();

    LauncherFrame.main(args);
  }
}
