package ee.twentyten.core;

import java.util.Locale;
import lombok.Getter;

@Getter
public enum EPlatform {
  OSX("macosx", "mac", "darwin"),
  LINUX("linux", "nix", "nux", "aix"),
  WINDOWS("windows", "win");

  private static final String OS_NAME = System.getProperty("os.name", "generic")
      .toLowerCase(Locale.getDefault());
  private final String name;
  private final String[] osNames;

  EPlatform(String name, String... osNames) {
    this.name = name;
    this.osNames = osNames;
  }

  public static EPlatform getByOSNames() {
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
