package ee.twentyten;

import java.util.Locale;

public enum EPlatform {
  MACOSX("mac", "darwin"),
  LINUX("nix", "nux", "aix"),
  WINDOWS("win");

  public static final String OS_NAME;

  static {
    OS_NAME = System.getProperty("os.name", "generic");
  }

  private final String[] osNames;

  EPlatform(String... osNames) {
    this.osNames = osNames;
  }

  public static EPlatform getPlatform() {
    EPlatform platform = null;
    for (EPlatform p : values()) {
      for (String os : p.osNames) {
        if (OS_NAME.toLowerCase(Locale.ROOT).contains(os)) {
          platform = p;
          break;
        }
      }
    }
    return platform;
  }
}
