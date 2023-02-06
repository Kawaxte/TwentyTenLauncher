package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LauncherHelper {

  public static final String LATEST_RELEASE_URL;
  public static final String ACCOUNT_SIGNUP_URL;
  public static final String CURRENT_VERSION;
  private static final String USER_HOME;
  private static final String APPDATA;
  private static final Map<EPlatform, File> WORKING_DIRECTORIES;
  private static final String LATEST_RELEASE_API_URL;

  static {
    LATEST_RELEASE_URL = "https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest";
    ACCOUNT_SIGNUP_URL = "https://signup.live.com/signup?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d&client_id=00000000402b5328&lic=1";
    USER_HOME = System.getProperty("user.home", ".");
    APPDATA = System.getenv("APPDATA");
    WORKING_DIRECTORIES = new HashMap<>();
    LATEST_RELEASE_API_URL = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";

    CURRENT_VERSION = getCurrentVersion(1, 2, 6, 23, true, 2);
  }

  private LauncherHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }


  public static String getCurrentVersion(int milestone, int month, int day, int year,
      boolean preRelease, int preVersion) {
    if (month < 1 || month > 12) {
      throw new IllegalArgumentException("month < 1 || month > 12");
    }
    if (day < 1 || day > 31) {
      throw new IllegalArgumentException("day < 1 || day > 31");
    }
    if (preRelease && preVersion < 0) {
      throw new IllegalArgumentException("preVersion < 0");
    }

    return String.format("%d.%01d.%02d%02d%s", milestone, month, day, year,
        preRelease ? String.format("_pre%d", preVersion) : "");
  }

  public static File getWorkingDirectory() {
    LauncherHelper.defineWorkingDirectoryForPlatform();

    EPlatform platformName = EPlatform.getPlatform();

    File workingDirectory = LauncherHelper.WORKING_DIRECTORIES.get(platformName);
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.exists()) {
      LogHelper.logInfo(LauncherHelper.class,
          String.format("\"%s\"", workingDirectory.getAbsolutePath()));

      boolean created = workingDirectory.mkdirs();
      if (!created) {
        LogHelper.logError(LauncherHelper.class, "Failed to create working directory");
        return null;
      }
    }
    return workingDirectory;
  }

  private static void defineWorkingDirectoryForPlatform() {
    File workingDirectoryForMacOsx = new File(
        String.format("%s/Library/Application Support", USER_HOME), "twentyten");
    WORKING_DIRECTORIES.put(EPlatform.MACOSX, workingDirectoryForMacOsx);

    File workingDirectoryForLinux = new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.LINUX, workingDirectoryForLinux);

    File workingDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    WORKING_DIRECTORIES.put(EPlatform.WINDOWS, workingDirectoryForWindows);
  }

  public static void setLookAndFeel() {
    try {
      String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
      UIManager.setLookAndFeel(lookAndFeel);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LogHelper.logError(LauncherHelper.class, "Failed to set look and feel", ulafe);
    } catch (ClassNotFoundException cnfe) {
      LogHelper.logError(LauncherHelper.class, "Can't find look and feel class", cnfe);
    } catch (InstantiationException ie) {
      LogHelper.logError(LauncherHelper.class, "Can't instantiate look and feel class", ie);
    } catch (IllegalAccessException iae) {
      LogHelper.logError(LauncherHelper.class, "Failed to access look and feel class", iae);
    }
  }

  public static boolean isOutdated() {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        RequestHelper.performJsonRequest(LATEST_RELEASE_API_URL, "GET",
            RequestHelper.jsonHeader, true);

        String latestVersion = RequestHelper.jsonHeader.get("tag_name");
        return !CURRENT_VERSION.equals(latestVersion);
      }
    };
    worker.execute();

    try {
      return worker.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LogHelper.logError(LauncherHelper.class, "Failed to interrupt current thread", ie);
    } catch (ExecutionException ee) {
      JOptionPane.showMessageDialog(null,
          String.format("An error occurred while checking for updates:%n%s", ee.getMessage()),
          "Error", JOptionPane.ERROR_MESSAGE);

      LogHelper.logError(LauncherHelper.class, "Failed to check for updates", ee);
    }
    return false;
  }
}
