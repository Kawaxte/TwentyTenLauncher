package ee.twentyten.core;

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
    for (EPlatform platform : values()) {
      for (String os : platform.osNames) {
        if (OS_NAME.contains(os)) {
          return platform;
        }
      }
    }
    return null;
  }
}
