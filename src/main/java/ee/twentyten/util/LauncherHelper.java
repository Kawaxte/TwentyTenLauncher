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
    LauncherHelper.mapWorkingDirectoryToPlatform();

    EPlatform platformName = EPlatform.getPlatform();

    File workingDirectory = LauncherHelper.workingDirectories.get(platformName);
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LoggerHelper.logError("Failed to get working directory", true);
      return null;
    }
    return workingDirectory;
  }

  public static void setLookAndFeel() {
    try {
      String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();

      UIManager.setLookAndFeel(systemLookAndFeel);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerHelper.logError("Can't set look and feel", ulafe, true);
    } catch (ClassNotFoundException cnfe) {
      LoggerHelper.logError("Can't find look and feel class", cnfe, true);
    } catch (InstantiationException ie) {
      LoggerHelper.logError("Can't instantiate look and feel class", ie, true);
    } catch (IllegalAccessException iae) {
      LoggerHelper.logError("Can't access look and feel class", iae, true);
    }
  }

  private static void mapWorkingDirectoryToPlatform() {
    File macosxFile = new File(
        String.format("%s/Library/Application Support", userHome), "twentyten");
    File linuxFile = new File(userHome, ".twentyten");
    File windowsAppdataFile = new File(appData, ".twentyten");
    File windowsFile = new File(userHome, ".twentyten");

    LauncherHelper.workingDirectories.put(EPlatform.MACOSX, macosxFile);
    LauncherHelper.workingDirectories.put(EPlatform.LINUX, linuxFile);
    LauncherHelper.workingDirectories.put(EPlatform.WINDOWS,
        appData != null ? windowsAppdataFile : windowsFile);
  }
}
