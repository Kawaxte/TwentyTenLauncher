package ee.twentyten.util;

public final class SystemUtils {

  public static String lineSeparator;
  public static String launcherVersion;
  public static String osVersion;

  static {
    SystemUtils.lineSeparator = System.getProperty("line.separator");
    SystemUtils.launcherVersion = System.getProperty("ee.twentyten.launcher.version");
    SystemUtils.osVersion = System.getProperty("os.version");
  }

  private SystemUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void setLauncherVersion(int major, int day, int month, int year, int preRelease,
      boolean isPreRelease) {
    if (major > 99 || major < 1) {
      throw new IllegalArgumentException("major > 99 || major < 1");
    }
    if (day > 31 || day < 1) {
      throw new IllegalArgumentException("day > 31 || day < 1");
    }
    if (month > 12 || month < 1) {
      throw new IllegalArgumentException("month > 12 || month < 1");
    }
    if (year > 99 || year < 23) {
      throw new IllegalArgumentException("year > 99 || year < 23");
    }
    if (preRelease > 99 || preRelease <= 0 && isPreRelease) {
      throw new IllegalArgumentException("preRelease > 99 || preRelease <= 0 && isPreRelease");
    }

    String currentLauncherVersion =
        isPreRelease ? String.format("%d.%d.%02d%02d_pre%d", major, month, day, year, preRelease)
            : String.format("%d.%d.%02d%02d", major, month, day, year);
    if (SystemUtils.launcherVersion == null) {
      System.setProperty("ee.twentyten.launcher.version", currentLauncherVersion);
      SystemUtils.launcherVersion = currentLauncherVersion;
    }
  }
}
