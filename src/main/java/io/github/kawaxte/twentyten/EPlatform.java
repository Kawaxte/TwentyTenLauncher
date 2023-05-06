package io.github.kawaxte.twentyten;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public enum EPlatform {
  LINUX("aix,", "nix", "nux"),
  MACOS("darwin", "mac"),
  WINDOWS("win");

  public static final String OS_NAME;

  static {
    OS_NAME = System.getProperty("os.name");
  }

  private final List<String> osNames;

  EPlatform(String... osNames) {
    this.osNames = Collections.unmodifiableList(Arrays.asList(osNames));
  }

  public static EPlatform getPlatform() {
    return Arrays.stream(values())
        .filter(
            platform ->
                platform.osNames.stream()
                    .anyMatch(osName -> OS_NAME.toLowerCase(Locale.ROOT).contains(osName)))
        .findFirst()
        .orElse(null);
  }

  public static boolean isWindows() {
    return Objects.equals(WINDOWS, getPlatform());
  }

  public static boolean isMacOS() {
    return Objects.equals(MACOS, getPlatform());
  }

  public static boolean isLinux() {
    return Objects.equals(LINUX, getPlatform());
  }
}
