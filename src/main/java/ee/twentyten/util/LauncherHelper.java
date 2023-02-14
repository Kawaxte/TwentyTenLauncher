package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LauncherHelper {

  static String userHome;
  static String appData;
  static Map<EPlatform, File> workingDirectories;

  static {
    LauncherHelper.userHome = System.getProperty("user.home", ".");
    LauncherHelper.appData = System.getenv("APPDATA");

    LauncherHelper.workingDirectories = new HashMap<>();
  }

  private LauncherHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static File getWorkingDirectory() {
    /* Maps the working directory to the current platform if it has not
     * already been mapped */
    LauncherHelper.mapWorkingDirectoryToPlatform();

    /* Gets the current platform */
    EPlatform platformName = EPlatform.getPlatform();

    /* Gets the working directory for the current platform */
    File workingDirectory = LauncherHelper.workingDirectories.get(
        platformName
    );

    /* 'NullPointerException' will be thrown here because the working
     * directory should never be null as it is needed for the launcher. */
    Objects.requireNonNull(
        workingDirectory,
        "workingDirectory == null!"
    );

    /* A log message will be written to the console if the working directory
      does not exist and couldn't be created. */
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LoggerHelper.logError(
          "Failed to get working directory",
          true
      );
      return null;
    }
    return workingDirectory;
  }

  public static void setLookAndFeel() {
    try {

      /* Gets the system look and feel class name */
      String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();

      /* Sets the look and feel to the system look and feel */
      UIManager.setLookAndFeel(systemLookAndFeel);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerHelper.logError(
          "Can't set look and feel",
          ulafe, true
      );
    } catch (ClassNotFoundException cnfe) {
      LoggerHelper.logError(
          "Can't find look and feel class",
          cnfe, true);
    } catch (InstantiationException ie) {
      LoggerHelper.logError(
          "Can't instantiate look and feel class",
          ie, true
      );
    } catch (IllegalAccessException iae) {
      LoggerHelper.logError(
          "Can't access look and feel class",
          iae, true
      );
    }
  }

  private static void mapWorkingDirectoryToPlatform() {

    /* Creates the working directory for the current platform if it has
     * not already been created */
    File macosxFile = new File(String.format(
        "%s/Library/Application Support",
        userHome), "twentyten"
    );
    File linuxFile = new File(
        userHome, ".twentyten"
    );
    File windowsAppdataFile = new File(
        appData, ".twentyten"
    );
    File windowsFile = new File(
        userHome, ".twentyten"
    );

    /* Maps the working directory to the current platform */
    LauncherHelper.workingDirectories.put(
        EPlatform.MACOSX, macosxFile
    );
    LauncherHelper.workingDirectories.put(
        EPlatform.LINUX, linuxFile
    );
    LauncherHelper.workingDirectories.put(
        EPlatform.WINDOWS,
        appData != null
            ? windowsAppdataFile
            : windowsFile
    );
  }
}
