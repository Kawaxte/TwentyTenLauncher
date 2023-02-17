package ee.twentyten.util;

import ee.twentyten.log.LauncherLogger;
import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerHelper {

  public static boolean isUsingDebugger;

  static {
    LoggerHelper.isUsingDebugger = ManagementFactory.getRuntimeMXBean()
        .getInputArguments().toString().contains("-agentlib:jdwp");

    LauncherLogger.instance = new LauncherLogger();
  }

  private LoggerHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static String getCallerClassName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    return elements[3].getClassName();
  }

  private static String getCallerMethodName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    return elements[3].getMethodName();
  }

  public static void logInfo(String message, boolean isWrittenToFile) {
    if (LoggerHelper.isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.info(
          String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }
    if (isWrittenToFile) {
      String output = String.format("[INFO] %s - %s",
          LoggerHelper.getCallerClassName(), message);

      LauncherLogger.instance.writeLog(output);
    }
  }

  public static void logWarn(String message, boolean isWrittenToFile) {
    if (LoggerHelper.isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.warn(
          String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }
    if (isWrittenToFile) {
      String output = String.format("[WARN] %s - %s",
          LoggerHelper.getCallerClassName(), message);

      LauncherLogger.instance.writeLog(output);
    }
  }

  public static void logError(String message, boolean isWrittenToFile) {
    if (LoggerHelper.isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.error(
          String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }
    if (isWrittenToFile) {
      String output = String.format("[ERROR] %s - %s",
          LoggerHelper.getCallerClassName(), message);

      LauncherLogger.instance.writeLog(output);
    }
  }

  public static void logError(String message, Throwable t,
      boolean isWrittenToFile) {
    Throwable cause = t.getCause() != null ? t.getCause() : t;

    if (LoggerHelper.isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.error(
          String.format("%s: %s", LoggerHelper.getCallerMethodName(), message),
          cause);
    }
    if (isWrittenToFile) {
      String output = String.format("[ERROR] %s - %s",
          LoggerHelper.getCallerClassName(), message);

      LauncherLogger.instance.writeLog(output, cause);
    }
  }
}
