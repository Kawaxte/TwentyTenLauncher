package ee.twentyten.util;

import ee.twentyten.request.EMethod;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.json.JSONObject;

public final class LauncherVersionHelper {

  static String latestReleaseUrl;

  static {
    LauncherVersionHelper.latestReleaseUrl =
        "https://api.github.com/" + "repos/" + "sojlabjoi/"
            + "AlphacraftLauncher/" + "releases/" + "latest";
  }

  private LauncherVersionHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getCurrentVersion(int majorVersion, int month, int day,
      int year, boolean isPreRelease, int preReleaseVersion) {
    LauncherVersionHelper.validateMonth(month);
    LauncherVersionHelper.validateDay(day);
    if (isPreRelease) {
      LauncherVersionHelper.validatePreReleaseVersion(preReleaseVersion);
    }

    String preReleaseString = LauncherVersionHelper.formatPreReleaseString(
        isPreRelease, preReleaseVersion);
    String currentVersion = String.format("%d.%d.%02d%02d%s", majorVersion,
        month, day, year, preReleaseString);

    System.setProperty("ee.twentyten.version", currentVersion);
  }

  public static boolean isLauncherOutdated() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Boolean> future = executor.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() {
        JSONObject result = RequestHelper.performJsonRequest(
            LauncherVersionHelper.latestReleaseUrl, EMethod.GET,
            RequestHelper.jsonHeader);
        Objects.requireNonNull(result, "result == null!");

        String latestVersion = result.getString("tag_name");
        String currentVersion = System.getProperty("ee.twentyten.version");
        return !Objects.equals(currentVersion, latestVersion);
      }
    });

    try {
      return future.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException ee) {
      LoggerHelper.logError("Failed to check for launcher updates", ee, true);
    }
    executor.shutdown();
    return false;
  }

  private static void validateMonth(int month) {
    if (month < 1 || month > 12) {
      Throwable iae = new IllegalArgumentException(
          String.format("Invalid month: %d", month));

      LoggerHelper.logError(iae.getMessage(), iae, true);
    }
  }

  private static void validateDay(int day) {
    if (day < 1 || day > 31) {
      Throwable iae = new IllegalArgumentException(
          String.format("Invalid day: %d", day));

      LoggerHelper.logError(iae.getMessage(), iae, true);
    }
  }

  private static void validatePreReleaseVersion(int preReleaseVersion) {
    if (preReleaseVersion < 0) {
      Throwable iae = new IllegalArgumentException(
          String.format("Invalid pre-release version: %d", preReleaseVersion));

      LoggerHelper.logError(iae.getMessage(), iae, true);
    }
  }

  private static String formatPreReleaseString(boolean isPreRelease,
      int preReleaseVersion) {
    String preReleaseString = String.format("_pre%d", preReleaseVersion);
    return isPreRelease ? preReleaseString : "";
  }
}
