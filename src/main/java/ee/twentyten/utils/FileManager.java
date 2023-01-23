package ee.twentyten.utils;

import ee.twentyten.core.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FileManager {

  private static final String USER_HOME = System.getProperty("user.home", ".");
  private static final String APPDATA = System.getenv("APPDATA");
  private static Map<EPlatform, File> gameDirectory;

  static {
    FileManager.gameDirectory = new HashMap<>();

    File gameDirectoryForOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME), "twentyten");
    FileManager.gameDirectory.put(EPlatform.OSX, gameDirectoryForOsx);

    File gameDirectoryForLinux = new File(USER_HOME, ".twentyten");
    FileManager.gameDirectory.put(EPlatform.LINUX, gameDirectoryForLinux);

    File gameDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    FileManager.gameDirectory.put(EPlatform.WINDOWS, gameDirectoryForWindows);
  }

  private FileManager() {
    throw new UnsupportedOperationException("Utility class is not instantiable");
  }

  public static File getGameDirectory() {
    EPlatform platformName = EPlatform.getByOSNames();

    File gameDirectory = FileManager.gameDirectory.get(platformName);
    Objects.requireNonNull(gameDirectory, "gameDirectory == null!");
    if (!gameDirectory.mkdirs() && !gameDirectory.exists()) {
      throw new RuntimeException("Can't create the game directory!");
    }
    return gameDirectory;
  }
}
