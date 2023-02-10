package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LauncherHelper {

  private static final String USER_HOME;
  private static final String APPDATA;
  private static final Map<EPlatform, File> WORKING_DIRECTORIES;
  private static final String LATEST_RELEASE_API_URL;

  static {
    LATEST_RELEASE_API_URL = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";

    USER_HOME = System.getProperty("user.home", ".");
    APPDATA = System.getenv("APPDATA");

    WORKING_DIRECTORIES = new HashMap<>();
  }

  private LauncherHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }


  public static void getCurrentVersion(int release, int month, int day, int year,
      boolean preRelease, int preVersion) {
    if (month < 1 || month > 12) {
      LoggerHelper.logError("month < 1 || month > 12", false);
      return;
    }
    if (day < 1 || day > 31) {
      LoggerHelper.logError("day < 1 || day > 31", false);
      return;
    }
    if (preRelease && preVersion < 0) {
      LoggerHelper.logError("preRelease && preVersion < 0", false);
      return;
    }

    String preReleaseString = preRelease ? String.format("_pre%d", preVersion) : "";
    String currentVersion = String.format("%d.%01d.%02d%02d%s", release, month, day, year,
        preReleaseString);
    System.setProperty("ee.twentyten.version", currentVersion);
  }

  private static void getWorkingDirectoryForPlatform() {
    File workingDirectoryForMacOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME), "twentyten");
    WORKING_DIRECTORIES.put(EPlatform.MACOSX, workingDirectoryForMacOsx);

    File workingDirectoryForLinux = new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.LINUX, workingDirectoryForLinux);

    File workingDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.WINDOWS, workingDirectoryForWindows);
  }

  public static File getWorkingDirectory() {
    LauncherHelper.getWorkingDirectoryForPlatform();

    EPlatform platformName = EPlatform.getPlatform();

    File workingDirectory = LauncherHelper.WORKING_DIRECTORIES.get(platformName);
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.exists()) {
      LoggerHelper.logInfo(String.format("\"%s\"", workingDirectory.getAbsolutePath()), true);

      boolean created = workingDirectory.mkdirs();
      if (!created) {
        Throwable t = new Throwable("Failed to create working directory");

        LoggerHelper.logError(t.getMessage(), t, true);
        return null;
      }
    }
    return workingDirectory;
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerHelper.logError(ulafe.getMessage(), ulafe, true);
    } catch (ClassNotFoundException cnfe) {
      LoggerHelper.logError("Can't find look and feel class", cnfe, true);
    } catch (InstantiationException ie) {
      LoggerHelper.logError("Can't instantiate look and feel class", ie, true);
    } catch (IllegalAccessException iae) {
      LoggerHelper.logError("Can't access look and feel class", iae, true);
    }
  }

  public static boolean isOutdated() {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        RequestHelper.performJsonRequest(LATEST_RELEASE_API_URL, "GET", RequestHelper.jsonHeader,
            true);

        String currentVersion = System.getProperty("ee.twentyten.version");
        String latestVersion = RequestHelper.jsonHeader.get("tag_name");
        return !currentVersion.equals(latestVersion);
      }
    };
    worker.execute();

    try {
      return worker.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LoggerHelper.logError("Failed to interrupt current thread", ie, true);
    } catch (ExecutionException ee) {
      LoggerHelper.logError("Failed to check for updates", ee, true);
    }
    return false;
  }
}
