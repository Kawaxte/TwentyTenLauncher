package ee.twentyten;

import java.util.Locale;

public enum EPlatform {
  MACOSX("mac", "darwin"), LINUX("nix", "nux", "aix"), WINDOWS("win");

  public static final String GENERIC_OS_NAME;

  static {
    GENERIC_OS_NAME = System.getProperty("os.name", "generic");
  }

  private final String[] osNames;

  EPlatform(String... osNames) {
    this.osNames = osNames;
  }

  public static EPlatform getCurrentPlatform() {
    for (EPlatform platform : values()) {
      for (String osName : platform.osNames) {
        if (GENERIC_OS_NAME.toLowerCase(Locale.ROOT).contains(osName)) {
          return platform;
        }
      }
    }
    return null;
  }
}
