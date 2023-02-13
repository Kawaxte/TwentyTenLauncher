package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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

  /**
   * Prevents instantiation of this class.
   *
   * @throws UnsupportedOperationException if this method is called.
   */
  private LauncherHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  /**
   * Get the working directory for the current platform.
   * <p>
   * The working directory is determined by the platform being used and will be
   * either `~/Library/Application Support/twentyten` on MacOSX, `~/.twentyten`
   * on Linux, or `%APPDATA%/.twentyten` on Windows.
   * <p>
   * If the working directory does not exist, this method will attempt to create
   * it. If it cannot be created, an error will be logged and `null` will be
   * returned.
   *
   * @return The working directory for the current platform, or `null` if it
   * cannot be determined or created.
   */
  public static File getWorkingDirectory() {
    LauncherHelper.mapWorkingDirectoryToPlatform();

    EPlatform platformName = EPlatform.getPlatform();
    File workingDirectory = LauncherHelper.workingDirectories.get(platformName);
    if (workingDirectory == null) {
      String notFoundError = "No working directory found";

      LoggerHelper.logError(notFoundError, true);
      return null;
    }
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      String creationError = "Failed to create working directory";

      LoggerHelper.logError(creationError, true);
      return null;
    }

    return workingDirectory;
  }

  /**
   * This method sets the look and feel for the application. It tries to set the
   * look and feel to the system look and feel.
   */
  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerHelper.logError(ulafe.getMessage(), ulafe, true);
    } catch (ClassNotFoundException cnfe) {
      String notFoundError = "Can't find look and feel class";

      LoggerHelper.logError(notFoundError, cnfe, true);
    } catch (InstantiationException ie) {
      String instantiationError = "Can't instantiate look and feel class";

      LoggerHelper.logError(instantiationError, ie, true);
    } catch (IllegalAccessException iae) {
      String accessError = "Can't access look and feel class";

      LoggerHelper.logError(accessError, iae, true);
    }
  }

  /**
   * Maps the working directory of the application to the corresponding
   * platform.
   *
   * <p>For MACOSX, the working directory will be located at:
   * <code>userHome/Library/Application Support/twentyten</code>
   *
   * <p>For LINUX, the working directory will be located at:
   * <code>userHome/.twentyten</code>
   *
   * <p>For WINDOWS, the working directory will be located at:
   * <code>appData/.twentyten</code>
   * If appData is null, it will be located at:
   * <code>userHome/.twentyten</code>
   */
  private static void mapWorkingDirectoryToPlatform() {
    LauncherHelper.workingDirectories.put(
        EPlatform.MACOSX,
        new File(String.format(
            "%s/Library/Application Support",
            userHome), "twentyten")
    );

    LauncherHelper.workingDirectories.put(
        EPlatform.LINUX,
        new File(userHome, ".twentyten")
    );

    File appDataFile = new File(appData, ".twentyten");
    LauncherHelper.workingDirectories.put(
        EPlatform.WINDOWS,
        appData != null
            ? appDataFile
            : new File(userHome, ".twentyten")
    );
  }
}
