package ee.twentyten.util;

import java.text.MessageFormat;

public final class SystemUtils {

  public static String lineSeparator;
  public static String launcherVersion;
  public static String osName;
  public static String osVersion;
  public static String javaVersion;

  static {
    SystemUtils.lineSeparator = System.getProperty("line.separator");
    SystemUtils.launcherVersion = System.getProperty("ee.twentyten.launcher.version");
    SystemUtils.osName = System.getProperty("os.name", "generic");
    SystemUtils.osVersion = System.getProperty("os.version");
    SystemUtils.javaVersion = System.getProperty("java.version");
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

    String currentLauncherVersion = MessageFormat.format(
        "{0,number}.{1,number}.{2,number,00}{3,number,00}{4}", major, month, day, year,
        isPreRelease ? MessageFormat.format("_pre{0,number}", preRelease) : "");
    if (SystemUtils.launcherVersion == null) {
      System.setProperty("ee.twentyten.launcher.version", currentLauncherVersion);
      SystemUtils.launcherVersion = currentLauncherVersion;
    }
  }
}
