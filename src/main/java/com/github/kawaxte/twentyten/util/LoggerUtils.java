package com.github.kawaxte.twentyten.util;

import com.github.kawaxte.twentyten.util.LauncherUtils.EPlatform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class LoggerUtils {

  private LoggerUtils() {
  }

  public static Path getLogFile(Path p, String name) {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(Objects.equals(EPlatform.getPlatform(), EPlatform.WINDOWS) ? ".txt" : ".log");

    File logFile = p.resolve(sb.toString()).toFile();
    try {
      if (!logFile.exists() && !logFile.createNewFile()) {
        throw new IOException(String.format("%s could not be created", logFile.getAbsolutePath()));
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } finally {
      logFile.deleteOnExit();
    }
    return logFile.toPath();
  }

  public enum ELevel {
    INFO,
    WARNING,
    ERROR
  }
}
