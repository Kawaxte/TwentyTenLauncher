package com.github.kawaxte.twentyten.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LauncherUtils {

  public static final boolean DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments()
      .toString().indexOf("-agentlib:jdwp") > 0;
  public static final Path WORKING_DIRECTORY;

  static {
    WORKING_DIRECTORY = getWorkingDirectory();
  }

  private LauncherUtils() {
  }

  private static Path getWorkingDirectory() {
    EPlatform platform = EPlatform.getPlatform();

    Map<EPlatform, Path> platformPaths = Collections.unmodifiableMap(
        new HashMap<EPlatform, Path>() {
          {
            put(EPlatform.LINUX, Paths.get(System.getProperty("user.home"), ".twentyten"));
            put(EPlatform.MACOS, Paths.get(System.getProperty("user.home"), "Library",
                "Application Support", "twentyten"));
            put(EPlatform.WINDOWS, Paths.get(System.getenv("APPDATA"), ".twentyten"));
          }
        });

    File workingDirectory = platformPaths.get(platform).toFile();
    return !workingDirectory.exists() && !workingDirectory.mkdirs() ? null
        : workingDirectory.toPath();
  }

  public enum EPlatform {
    LINUX("nux"),
    MACOS("mac"),
    WINDOWS("win");

    private final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    private final String[] osNames;

    EPlatform(String... osNames) {
      this.osNames = osNames;
    }

    public static EPlatform getPlatform() {
      return Arrays.stream(values())
          .filter(platform -> Arrays.stream(platform.osNames).anyMatch(platform.osName::contains))
          .findFirst().orElse(null);
    }
  }
}
