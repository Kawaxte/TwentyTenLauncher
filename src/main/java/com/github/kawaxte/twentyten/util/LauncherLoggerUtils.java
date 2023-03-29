package com.github.kawaxte.twentyten.util;

import com.github.kawaxte.twentyten.log.AbstractLauncherLogger;
import com.github.kawaxte.twentyten.util.LauncherUtils.EPlatform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class LauncherLoggerUtils {

  private LauncherLoggerUtils() {
  }

  public static Path getLogFile(Path p, String name) {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(EPlatform.isWindows() ? ".txt" : ".log");

    File logFile = p.resolve(sb.toString()).toFile();
    try {
      if (!logFile.exists() && !logFile.createNewFile()) {
        return null;
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Could not create log file", ioe);
    } finally {
      logFile.deleteOnExit();
    }
    return logFile.toPath();
  }

  public static char[] getTimestamp() {
    Calendar calendar = Calendar.getInstance();
    return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()).toCharArray();
  }

  public static String getCallerClassName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(AbstractLauncherLogger.class.getName())) {
        return elements[i + 3].getClassName();
      }
    }
    return null;
  }

  public static String getCallerMethodName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(AbstractLauncherLogger.class.getName())) {
        return elements[i + 3].getMethodName();
      }
    }
    return null;
  }

  public static String getCallerLineNumber() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(AbstractLauncherLogger.class.getName())) {
        return String.valueOf(elements[i + 3].getLineNumber());
      }
    }
    return null;
  }

  public enum ELevel {
    INFO,
    WARNING,
    ERROR
  }
}
