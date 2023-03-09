package ee.twentyten;

import ee.twentyten.util.SystemUtils;
import java.util.Locale;

public enum EPlatform {
  MACOSX("mac", "darwin"), LINUX("nix", "nux", "aix"), WINDOWS("win");

  private final String[] osNames;

  EPlatform(String... osNames) {
    this.osNames = osNames;
  }

  public static EPlatform getPlatform() {
    for (EPlatform platform : values()) {
      for (String osName : platform.osNames) {
        if (SystemUtils.osName.toLowerCase(Locale.ROOT).contains(osName)) {
          return platform;
        }
      }
    }
    return null;
  }
}
