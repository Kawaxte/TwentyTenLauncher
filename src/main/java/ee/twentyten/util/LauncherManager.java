package ee.twentyten.util;

import ee.twentyten.launcher.EPlatform;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LauncherManager {

  public static final String LATEST_RELEASE_URL;
  public static final String ACCOUNT_SIGNUP_URL;
  private static final String USER_HOME;
  private static final String APPDATA;
  private static final Map<EPlatform, File> WORKING_DIRECTORIES;
  private static final String LATEST_RELEASE_API_URL;
  private static String currentVersion = null;

  static {
    LATEST_RELEASE_URL = "https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest";
    ACCOUNT_SIGNUP_URL = "https://signup.live.com/signup?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d&client_id=00000000402b5328&lic=1";
    USER_HOME = System.getProperty("user.home", ".");
    APPDATA = System.getenv("APPDATA");
    WORKING_DIRECTORIES = new HashMap<>();
    LATEST_RELEASE_API_URL = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";
  }

  private LauncherManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getCurrentVersion(int value, boolean isDevBuild) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int milestone = (year - 2022) % 10;

    String formattedYear = String.format("%02d", year % 100);
    String formattedMonth = String.format("%02d", month);
    String formattedDay = String.format("%02d", day);
    String formattedVersion = String.format("%d.%s.%s%s%s", milestone, formattedMonth, formattedDay,
        formattedYear, isDevBuild ? String.format("_pre%d", value) : "");
    LauncherManager.currentVersion = formattedVersion;
    return formattedVersion;
  }

  public static File getWorkingDirectory() {
    LauncherManager.defineWorkingDirectoryForPlatform();

    EPlatform platformName = EPlatform.getPlatform();

    File workingDirectory = LauncherManager.WORKING_DIRECTORIES.get(platformName);
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.exists()) {
      LoggingManager.logInfo(LauncherManager.class,
          String.format("\"%s\"", workingDirectory.getAbsolutePath()));

      boolean created = workingDirectory.mkdirs();
      if (!created) {
        LoggingManager.logError(LauncherManager.class, "Failed to create working directory");
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
      LoggingManager.logError(LauncherManager.class, "Failed to set look and feel", ulafe);
    } catch (ClassNotFoundException cnfe) {
      LoggingManager.logError(LauncherManager.class, "Can't find look and feel class", cnfe);
    } catch (InstantiationException ie) {
      LoggingManager.logError(LauncherManager.class, "Can't instantiate look and feel class",
          ie);
    } catch (IllegalAccessException iae) {
      LoggingManager.logError(LauncherManager.class, "Failed to access look and feel class",
          iae);
    }
  }

  public static boolean isOutdated() {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        String latestVersion = Objects.requireNonNull(
            RequestManager.sendJsonRequest(LATEST_RELEASE_API_URL, "GET",
                RequestManager.JSON_HEADER)).get("tag_name").toString();
        return !currentVersion.equals(latestVersion);
      }
    };
    worker.execute();

    try {
      return worker.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LoggingManager.logError(LauncherManager.class, "Failed to interrupt current thread", ie);
    } catch (ExecutionException ee) {
      JOptionPane.showMessageDialog(null,
          String.format("An error occurred while checking for updates:%n%s", ee.getMessage()),
          "Error", JOptionPane.ERROR_MESSAGE);

      LoggingManager.logError(LauncherManager.class, "Failed to check for updates", ee);
    }
    return false;
  }
}
