package ee.twentyten.utils;

import ee.twentyten.core.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FilesManager {

  private static final String USER_HOME = System.getProperty("user.home", ".");
  private static final String APPDATA = System.getenv("APPDATA");
  private static final Map<EPlatform, File> gameDirectories;

  static {
    gameDirectories = new HashMap<>();

    File gameDirectoryForOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME), "twentyten");
    gameDirectories.put(EPlatform.OSX, gameDirectoryForOsx);

    File gameDirectoryForLinux = new File(USER_HOME, ".twentyten");
    gameDirectories.put(EPlatform.LINUX, gameDirectoryForLinux);

    File gameDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    gameDirectories.put(EPlatform.WINDOWS, gameDirectoryForWindows);
  }

  private FilesManager() {
  }

  public static File getGameDirectory() {
    EPlatform platformName = EPlatform.getPlatform();

    File gameDirectory = FilesManager.gameDirectories.get(platformName);
    Objects.requireNonNull(gameDirectory, "gameDirectory == null!");
    if (!gameDirectory.mkdirs() && !gameDirectory.exists()) {
      throw new RuntimeException("Can't create the game directory!");
    }
    return gameDirectory;
  }
}
