package ee.twentyten.utils;

import ee.twentyten.core.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class LauncherManager {

  private static final String USER_HOME = System.getProperty("user.home", ".");
  private static final String APPDATA = System.getenv("APPDATA");
  private static Map<EPlatform, File> gameDirectory;

  static {
    LauncherManager.gameDirectory = new HashMap<>();

    File gameDirectoryForOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME), "twentyten");
    LauncherManager.gameDirectory.put(EPlatform.OSX, gameDirectoryForOsx);

    File gameDirectoryForLinux = new File(USER_HOME, ".twentyten");
    LauncherManager.gameDirectory.put(EPlatform.LINUX, gameDirectoryForLinux);

    File gameDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    LauncherManager.gameDirectory.put(EPlatform.WINDOWS, gameDirectoryForWindows);
  }

  private LauncherManager() {
  }

  public static File getGameDirectory() {
    EPlatform platformName = EPlatform.getByOSNames();

    File gameDirectory = LauncherManager.gameDirectory.get(platformName);
    Objects.requireNonNull(gameDirectory, "gameDirectory == null!");
    if (!gameDirectory.mkdirs() && !gameDirectory.exists()) {
      throw new RuntimeException("Can't create the game directory!");
    }
    return gameDirectory;
  }
}
