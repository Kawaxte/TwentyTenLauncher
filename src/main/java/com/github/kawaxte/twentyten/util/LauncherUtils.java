package com.github.kawaxte.twentyten.util;

import com.github.kawaxte.twentyten.log.LauncherLogger;
import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class LauncherUtils {

  public static boolean DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
      .indexOf("-agentlib:jdwp") > 0;
  public static final Path WORKING_DIRECTORY;

  static {
    WORKING_DIRECTORY = getWorkingDirectory();
  }

  private LauncherUtils() {
    throw new Error(MessageFormat.format("{0} is not instantiable", this.getClass().getName()));
  }

  private static Path getWorkingDirectory() {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    Map<EPlatform, Path> workingDirectories = Collections.unmodifiableMap(
        new HashMap<EPlatform, Path>() {
          {
            put(EPlatform.LINUX, Paths.get(userHome, ".twentyten"));
            put(EPlatform.OSX, Paths.get(userHome, "Library", "Application Support", "twentyten"));
            put(EPlatform.WINDOWS, Paths.get(appData, ".twentyten"));
          }
        });

    File workingDirectory = workingDirectories.get(EPlatform.getPlatform()).toFile();
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LauncherLogger.log(ELevel.WARNING, "{0} could not be created", workingDirectory);
      return null;
    }
    return workingDirectory.toPath();
  }

  public enum EPlatform {
    LINUX("nux"),
    OSX("mac"),
    WINDOWS("win");

    private static final String OS_NAME = System.getProperty("os.name");
    private final String[] osNames;

    EPlatform(String... osNames) {
      this.osNames = osNames;
    }

    public static EPlatform getPlatform() {
      return Arrays.stream(values()).filter(platform -> Arrays.stream(platform.osNames)
              .anyMatch(OS_NAME.toLowerCase(Locale.ROOT)::contains))
          .findFirst()
          .orElse(null);
    }

    public static boolean isWindows() {
      return Objects.equals(WINDOWS, getPlatform());
    }

    public static boolean isOSX() {
      return Objects.equals(OSX, getPlatform());
    }

    public static boolean isLinux() {
      return Objects.equals(LINUX, getPlatform());
    }
  }
}
