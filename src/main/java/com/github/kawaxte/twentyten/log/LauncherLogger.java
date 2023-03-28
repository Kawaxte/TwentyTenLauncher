package com.github.kawaxte.twentyten.log;

import com.github.kawaxte.twentyten.util.LauncherUtils;
import com.github.kawaxte.twentyten.util.LoggerUtils;
import com.github.kawaxte.twentyten.util.LoggerUtils.ELevel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public final class LauncherLogger {

  private LauncherLogger() {
  }

  public static void log(ELevel level, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatMessage(level, message, args));
    if (LauncherUtils.DEBUG) {
      switch (level) {
        case WARNING:
        case ERROR:
          System.err.print(sb);
          break;
        default:
          System.out.print(sb);
          break;
      }
    }

    File logFile = LoggerUtils.getLogFile(LauncherUtils.WORKING_DIRECTORY, "launcher_log").toFile();
    try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
      fos.write(sb.toString().getBytes());
      fos.flush();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public static void log(Throwable t, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatMessage(ELevel.ERROR, message, args));
    if (t != null) {
      sb.append(t.getClass().getName());
      sb.append(":\u00A0");
      sb.append(t.getMessage()).append(System.lineSeparator());

      Arrays.stream(t.getStackTrace()).forEachOrdered(element -> {
        sb.append("\tat\u00A0");
        sb.append(element.toString()).append(System.lineSeparator());
      });
    }
    if (LauncherUtils.DEBUG) {
      System.err.print(sb);
    }

    File logFile = LoggerUtils.getLogFile(LauncherUtils.WORKING_DIRECTORY, "launcher_log").toFile();
    try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
      fos.write(sb.toString().getBytes());
      fos.flush();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static char[] getTimestamp() {
    Calendar calendar = Calendar.getInstance();
    return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()).toCharArray();
  }

  static String getCallerClassName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(LauncherLogger.class.getName())) {
        return elements[i + 3].getClassName();
      }
    }
    return null;
  }

  static String getCallerMethodName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(LauncherLogger.class.getName())) {
        return elements[i + 3].getMethodName();
      }
    }
    return null;
  }

  static String getCallerLineNumber() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = 0; i < elements.length; i++) {
      StackTraceElement element = elements[i];
      if (element.getClassName().equals(LauncherLogger.class.getName())) {
        return String.valueOf(elements[i + 3].getLineNumber());
      }
    }
    return null;
  }

  static String formatMessage(ELevel level, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(LauncherLogger.getTimestamp());
    sb.append("\u00A0");
    sb.append(level);
    sb.append("]\u00A0");
    sb.append(LauncherLogger.getCallerClassName());
    sb.append("::");
    sb.append(LauncherLogger.getCallerMethodName());
    sb.append("(");
    sb.append(LauncherLogger.getCallerLineNumber());
    sb.append(")\u00A0");
    sb.append("-\u00A0");
    return args.length == 0
        ? sb.append(message).append(System.lineSeparator()).toString()
        : sb.append(MessageFormat.format(message, args)).append(System.lineSeparator()).toString();
  }
}
