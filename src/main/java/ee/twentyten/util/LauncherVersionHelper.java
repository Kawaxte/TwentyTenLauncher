package ee.twentyten.util;

import ee.twentyten.request.EMethod;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class LauncherVersionHelper {

  static String latestReleaseUrl;

  static {
    LauncherVersionHelper.latestReleaseUrl =
        "https://api.github.com/"
            + "repos/"
            + "sojlabjoi/"
            + "AlphacraftLauncher/"
            + "releases/"
            + "latest";
  }

  /**
   * Prevents instantiation of this class.
   *
   * @throws UnsupportedOperationException if this method is called.
   */
  private LauncherVersionHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  /**
   * Returns the current version of the software in the format:
   * x.MM.ddyy_pre{preReleaseVersion}
   *
   * @param majorVersion      the major version number of the software
   * @param month             the month of the release
   * @param day               the day of the release
   * @param year              the year of the release
   * @param isPreRelease      true if this is a pre-release version, false
   *                          otherwise
   * @param preReleaseVersion the pre-release version number
   * @throws IllegalArgumentException if any of the parameters are invalid
   */
  public static void getCurrentVersion(
      int majorVersion,
      int month,
      int day,
      int year,
      boolean isPreRelease,
      int preReleaseVersion
  ) {
    LauncherVersionHelper.validateMonth(month);
    LauncherVersionHelper.validateDay(day);
    if (isPreRelease) {
      LauncherVersionHelper.validatePreReleaseVersion(preReleaseVersion);
    }

    String preReleaseString = LauncherVersionHelper.formatPreReleaseString(
        isPreRelease, preReleaseVersion);
    String currentVersion = String.format(
        "%d.%02d.%02d%02d%s",
        majorVersion, month, day, year, preReleaseString
    );

    System.setProperty("ee.twentyten.version", currentVersion);
  }

  /**
   * Determines if the launcher is outdated.
   *
   * @return a boolean indicating whether the launcher is outdated or not
   */
  public static boolean isLauncherOutdated() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Boolean> future = executor.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() {
        RequestHelper.performJsonRequest(
            LauncherVersionHelper.latestReleaseUrl,
            EMethod.GET,
            RequestHelper.jsonHeader);

        String currentVersion = System.getProperty("ee.twentyten.version");
        String latestVersion = RequestHelper.jsonHeader.get("tag_name");
        return !currentVersion.equals(latestVersion);
      }
    });

    try {
      future.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LoggerHelper.logError("Failed to interrupt current thread", ie, true);
    } catch (ExecutionException ee) {
      LoggerHelper.logError("Failed to check for launcher updates", ee, true);
    }
    executor.shutdown();
    return false;
  }

  /**
   * Validates the given month.
   *
   * @param month The month to validate.
   */
  private static void validateMonth(
      int month
  ) {
    if (month < 1 || month > 12) {
      Throwable t = new Throwable(String.format(
          "Invalid month: %d",
          month)
      );

      LoggerHelper.logError(t.getMessage(), t, true
      );
    }
  }

  /**
   * Validates the given day.
   *
   * @param day The day to validate.
   */
  private static void validateDay(
      int day
  ) {
    if (day < 1 || day > 31) {
      Throwable t = new Throwable(String.format(
          "Invalid day: %d",
          day)
      );

      LoggerHelper.logError(t.getMessage(), t, true
      );
    }
  }

  /**
   * Validates the given pre-release version.
   *
   * @param preReleaseVersion The pre-release version to validate.
   */
  private static void validatePreReleaseVersion(
      int preReleaseVersion
  ) {
    if (preReleaseVersion < 0) {
      Throwable t = new Throwable(String.format(
          "Invalid pre-release version: %d",
          preReleaseVersion)
      );

      LoggerHelper.logError(t.getMessage(), t, false);
    }
  }

  /**
   * Formats the pre-release string for the version.
   *
   * @param isPreRelease      Whether the version is a pre-release version.
   * @param preReleaseVersion The pre-release version.
   * @return A string representation of the pre-release version.
   */
  private static String formatPreReleaseString(
      boolean isPreRelease,
      int preReleaseVersion
  ) {
    return isPreRelease
        ? "_pre" + preReleaseVersion
        : "";
  }
}
