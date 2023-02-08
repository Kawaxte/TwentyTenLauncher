package ee.twentyten.util;

import ee.twentyten.log.LauncherLogger;
import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerHelper {

  public static final boolean DEBUG_MODE;
  private static final LauncherLogger LOGGER;

  static {
    DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
        .contains("-agentlib:jdwp");

    LOGGER = new LauncherLogger();
  }

  private LoggerHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static String getCallerClassName() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    return stackTraceElements[3].getClassName();
  }

  private static String getCallerMethodName() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    return stackTraceElements[3].getMethodName();
  }

  public static void logInfo(String message, boolean written) {
    if (DEBUG_MODE) {
      Logger logger = LoggerFactory.getLogger(LoggerHelper.getCallerClassName());
      logger.info(String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }

    if (written) {
      String outputMessage = String.format("[INFO] %s - %s", LoggerHelper.getCallerClassName(),
          message);
      LOGGER.writeLog(outputMessage);
    }
  }

  public static void logWarn(String message, boolean written) {
    if (DEBUG_MODE) {
      Logger logger = LoggerFactory.getLogger(LoggerHelper.getCallerClassName());
      logger.warn(String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }

    if (written) {
      String outputMessage = String.format("[WARN] %s - %s", LoggerHelper.getCallerClassName(),
          message);
      LOGGER.writeLog(outputMessage);
    }
  }

  public static void logError(String message, boolean written) {
    if (DEBUG_MODE) {
      Logger logger = LoggerFactory.getLogger(LoggerHelper.getCallerClassName());
      logger.error(String.format("%s: %s", LoggerHelper.getCallerMethodName(), message));
    }

    if (written) {
      String outputMessage = String.format("[ERROR] %s - %s", LoggerHelper.getCallerClassName(),
          message);
      LOGGER.writeLog(outputMessage);
    }
  }

  public static void logError(String message, Throwable t, boolean written) {
    Throwable cause = t.getCause() != null ? t.getCause() : t;
    if (DEBUG_MODE) {
      Logger logger = LoggerFactory.getLogger(LoggerHelper.getCallerClassName());
      logger.error(String.format("%s: %s", LoggerHelper.getCallerMethodName(), message), cause);
    }

    if (written) {
      String outputMessage = String.format("[ERROR] %s - %s", LoggerHelper.getCallerClassName(),
          message);
      LOGGER.writeLog(outputMessage, cause);
    }
  }
}
