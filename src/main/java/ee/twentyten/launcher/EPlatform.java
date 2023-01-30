package ee.twentyten.launcher;

import java.util.Locale;

public enum EPlatform {
  OSX("mac", "darwin"),
  LINUX("nix", "nux", "aix"),
  WINDOWS("win");

  private static final String OS_NAME = System.getProperty("os.name", "generic")
      .toLowerCase(Locale.getDefault());
  private final String[] osNames;

  EPlatform(String... osNames) {
    this.osNames = osNames;
  }

  public static EPlatform getPlatform() {
    EPlatform platform = null;
    for (EPlatform p : values()) {
      for (String os : p.osNames) {
        if (OS_NAME.contains(os)) {
          platform = p;
          break;
        }
      }
    }
    return platform;
  }
}
