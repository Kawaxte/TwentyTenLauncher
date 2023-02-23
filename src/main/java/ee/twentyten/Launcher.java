package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.SystemUtils;

public class Launcher {

  public static void main(String... args) {
    if (LauncherUtils.MIN_MEMORY > LauncherUtils.MAX_MEMORY) {
      LauncherUtils.buildLowMemoryProcess();
    }

    SystemUtils.setLauncherVersion(1, 23, 2, 23, 1, true);

    LauncherUtils.readFromConfig();
    LauncherFrame.main(args);
  }
}
