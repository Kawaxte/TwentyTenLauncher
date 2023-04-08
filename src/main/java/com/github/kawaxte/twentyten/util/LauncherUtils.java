package com.github.kawaxte.twentyten.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherUtils {

  public static final Path WORKING_DIR_PATH;
  public static Logger logger;

  static {
    WORKING_DIR_PATH = getWorkingDir();

    logger = LogManager.getLogger(LauncherUtils.class);
  }

  private LauncherUtils() {
  }

  public static Path getWorkingDir() {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    var workingDirLookup = Collections.unmodifiableMap(
        new HashMap<EPlatform, Path>() {
          {
            put(EPlatform.LINUX, Paths.get(
                userHome, ".twentyten"));
            put(EPlatform.MACOS, Paths.get(
                userHome, "Library", "Application Support", "twentyten"));
            put(EPlatform.WINDOWS, Paths.get(
                appData, ".twentyten"));
          }
        });

    var workingDirFile = workingDirLookup.get(EPlatform.getPlatform()).toFile();
    if (!workingDirFile.exists() && !workingDirFile.mkdirs()) {
      logger.warn("Directory \"{}\" could not be created", workingDirFile.getName());
      return null;
    }
    return workingDirFile.toPath();
  }

  public enum EPlatform {
    LINUX("nux"),
    MACOS("mac"),
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

    public static boolean isMacOs() {
      return Objects.equals(MACOS, getPlatform());
    }

    public static boolean isLinux() {
      return Objects.equals(LINUX, getPlatform());
    }
  }
}
