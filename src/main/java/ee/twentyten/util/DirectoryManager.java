package ee.twentyten.util;

import ee.twentyten.core.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DirectoryManager {

  private static final String USER_HOME;
  private static final String APPDATA;
  private static final Map<EPlatform, File> WORKING_DIRECTORIES;

  static {
    USER_HOME = System.getProperty("user.home", ".");
    APPDATA = System.getenv("APPDATA");

    WORKING_DIRECTORIES = new HashMap<>();
    DirectoryManager.getWorkingDirectoryPerPlatform();
  }

  private DirectoryManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static File getWorkingDirectory() {
    EPlatform platformName = EPlatform.getPlatform();

    File workingDirectory = DirectoryManager.WORKING_DIRECTORIES.get(platformName);
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.mkdirs() && !workingDirectory.exists()) {
      throw new SecurityException("Failed to create working directory");
    }
    return workingDirectory;
  }

  private static void getWorkingDirectoryPerPlatform() {
    File workingDirectoryForOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME),
        "twentyten");
    WORKING_DIRECTORIES.put(EPlatform.OSX, workingDirectoryForOsx);

    File workingDirectoryForLinux = new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.LINUX, workingDirectoryForLinux);

    File workingDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.WINDOWS, workingDirectoryForWindows);
  }
}
