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
    /* If the working directory has not been mapped to the current platform,
     * map it. */
    LauncherHelper.mapWorkingDirectoryToPlatform();

    /* Get the name of the current platform. */
    EPlatform platformName = EPlatform.getPlatform();

    /* Get the working directory for the current platform. */
    File workingDirectory = LauncherHelper.workingDirectories.get(platformName);

    /* If the working directory is null, log the error and return null. */
    if (workingDirectory == null) {

      /* Create a string for the error message. */
      String nullString = "Failed to find working directory";

      /* Log the error. */
      LoggerHelper.logError(nullString, true);
      return null;
    }

    /* If the working directory does not exist, attempt to create it. */
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {

      /* Create a string for the error message. */
      String errorString = "Failed to create working directory";

      /* Log the error. */
      LoggerHelper.logError(errorString, true);
      return null;
    }
    return workingDirectory;
  }

  /**
   * This method sets the look and feel for the application. It tries to set the
   * look and feel to the system look and feel.
   */
  public static void setLookAndFeel() {

    /* Try to set the look and feel to the system look and feel. */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException ulafe) {

      /* If the look and feel is not supported, log the error. */
      LoggerHelper.logError(ulafe.getMessage(), ulafe, true);
    } catch (ClassNotFoundException cnfe) {

      /* Create a string for the error message. */
      String classNotFound = "Can't find look and feel class";

      /* If the look and feel class is not found, log the error. */
      LoggerHelper.logError(classNotFound, cnfe, true);
    } catch (InstantiationException ie) {

      /* Create a string for the error message. */
      String instantiation = "Can't instantiate look and feel class";

      /* If the look and feel class can't be instantiated, log the error. */
      LoggerHelper.logError(instantiation, ie, true);
    } catch (IllegalAccessException iae) {

      /* Create a string for the error message. */
      String illegalAccess = "Can't access look and feel class";

      /* If the look and feel class can't be accessed, log the error. */
      LoggerHelper.logError(illegalAccess, iae, true);
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

    /* If the working directory has already been mapped to the current
     * platform, return. */
    LauncherHelper.workingDirectories.put(
        EPlatform.MACOSX,
        new File(String.format(
            "%s/Library/Application Support",
            userHome), "twentyten")
    );

    /* If the working directory has already been mapped to the current
     * platform, return. */
    LauncherHelper.workingDirectories.put(
        EPlatform.LINUX,
        new File(userHome, ".twentyten")
    );

    /* Create a file for the appData directory. */
    File appDataFile = new File(appData, ".twentyten");

    /* If the working directory has already been mapped to the current
     * platform, return. */
    LauncherHelper.workingDirectories.put(
        EPlatform.WINDOWS,
        appData != null
            ? appDataFile
            : new File(userHome, ".twentyten")
    );
  }
}
