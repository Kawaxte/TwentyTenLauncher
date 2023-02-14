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
        isPreRelease,
        preReleaseVersion
    );
    String currentVersion = String.format(
        "%d.%02d.%02d%02d%s",
        majorVersion, month, day, year, preReleaseString
    );

    System.setProperty("ee.twentyten.version", currentVersion);
  }

  /**
   * Checks if the current launcher version is outdated by comparing it to the
   * latest version on Github. The check is performed in a separate thread to
   * avoid blocking the main thread while waiting for a response from the Github
   * API.
   *
   * @return True if the launcher is outdated, false if it is up-to-date
   */
  public static boolean isLauncherOutdated() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Boolean> future = executor.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() {
        RequestHelper.performJsonRequest(
            LauncherVersionHelper.latestReleaseUrl,
            EMethod.GET,
            RequestHelper.jsonHeader
        );

        String currentVersion = System.getProperty("ee.twentyten.version");
        String latestVersion = RequestHelper.jsonHeader.get("tag_name");
        return !currentVersion.equals(latestVersion);
      }
    });

    try {
      future.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      String errorMessage = "Failed to interrupt current thread";

      LoggerHelper.logError(errorMessage, ie, true);
    } catch (ExecutionException ee) {
      String errorMessage = "Failed to check for launcher updates";

      LoggerHelper.logError(errorMessage, ee, true);
    }
    executor.shutdown();
    return false;
  }

  /**
   * Validates that the given month is within the valid range of 1 to 12. If the
   * month is invalid, an IllegalArgumentException is thrown.
   *
   * @param month the month to validate
   * @throws IllegalArgumentException if the month is less than 1 or greater
   *                                  than 12
   */
  private static void validateMonth(
      int month
  ) {
    if (month < 1 || month > 12) {
      Throwable ia = new IllegalArgumentException(String.format(
          "Invalid month: %d",
          month)
      );

      LoggerHelper.logError(ia.getMessage(), ia, true);
    }
  }

  /**
   * Validates that the given day is within the valid range of 1 to 31. If the
   * day is invalid, an IllegalArgumentException is thrown.
   *
   * @param day the day to validate
   * @throws IllegalArgumentException if the day is less than 1 or greater than
   *                                  31
   */
  private static void validateDay(
      int day
  ) {
    if (day < 1 || day > 31) {
      Throwable iae = new IllegalArgumentException(String.format(
          "Invalid day: %d",
          day)
      );

      LoggerHelper.logError(iae.getMessage(), iae, true);
    }
  }

  /**
   * Validates that the given pre-release version is greater than or equal to 0.
   * If the pre-release version is invalid, an IllegalArgumentException is
   * thrown.
   *
   * @param preReleaseVersion the pre-release version to validate
   * @throws IllegalArgumentException if the pre-release version is less than 0
   */
  private static void validatePreReleaseVersion(
      int preReleaseVersion
  ) {
    if (preReleaseVersion < 0) {
      Throwable iae = new IllegalArgumentException(String.format(
          "Invalid pre-release version: %d",
          preReleaseVersion)
      );

      LoggerHelper.logError(iae.getMessage(), iae, true);
    }
  }

  /**
   * Formats the pre-release version number and returns a string representation
   * of the pre-release string.
   *
   * @param isPreRelease      a boolean indicating if the version is a
   *                          pre-release version
   * @param preReleaseVersion the pre-release version number
   * @return a formatted string representing the pre-release version, or an
   * empty string if the version is not a pre-release version
   */
  private static String formatPreReleaseString(
      boolean isPreRelease,
      int preReleaseVersion
  ) {
    return isPreRelease
        ? String.format(
        "_pre%d",
        preReleaseVersion)
        : "";
  }
}
