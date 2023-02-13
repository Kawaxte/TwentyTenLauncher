package ee.twentyten.util;

import ee.twentyten.log.LauncherLogger;
import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for logging messages to the console and/or a file.
 *
 * <p>The class provides methods for logging info, warning, and error messages.
 * The log messages can be written to the console and/or a file.
 *
 * <p>This class is a utility class and can't be instantiated.
 */
public final class LoggerHelper {
  
  public static boolean isUsingDebugger;

  static {
    LoggerHelper.isUsingDebugger = ManagementFactory.getRuntimeMXBean()
        .getInputArguments()
        .toString()
        .contains("-agentlib:jdwp");

    LauncherLogger.instance = new LauncherLogger();
  }

  /**
   * Prevents instantiation of this class.
   *
   * @throws UnsupportedOperationException if this method is called.
   */
  private LoggerHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  /**
   * Retrieves the name of the class that is calling the method.
   *
   * @return The name of the class that is calling the method.
   */
  private static String getCallerClassName() {
    StackTraceElement[] elements = Thread.currentThread()
        .getStackTrace();
    return elements[3].getClassName();
  }

  /**
   * Retrieves the name of the method that is calling the method.
   *
   * @return The name of the method that is calling the method.
   */
  private static String getCallerMethodName() {
    StackTraceElement[] elements = Thread.currentThread()
        .getStackTrace();
    return elements[3].getMethodName();
  }

  /**
   * Logs the provided message as an informational message.
   *
   * @param message         The message to be logged.
   * @param isWrittenToFile Indicates whether the message should be written to a
   *                        file.
   */
  public static void logInfo(
      String message,
      boolean isWrittenToFile
  ) {
    if (isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.info(String.format(
          "%s: %s",
          LoggerHelper.getCallerMethodName(), message)
      );
    }

    if (isWrittenToFile) {
      String output = String.format(
          "[INFO] %s - %s",
          LoggerHelper.getCallerClassName(), message
      );

      LauncherLogger.getInstance().writeLog(output);
    }
  }

  /**
   * Logs the provided message as a warning message.
   *
   * @param message         The message to be logged.
   * @param isWrittenToFile Indicates whether the message should be written to a
   *                        file.
   */
  public static void logWarn(
      String message,
      boolean isWrittenToFile
  ) {
    if (isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.warn(String.format(
          "%s: %s",
          LoggerHelper.getCallerMethodName(), message)
      );
    }

    if (isWrittenToFile) {
      String output = String.format(
          "[WARN] %s - %s",
          LoggerHelper.getCallerClassName(), message
      );

      LauncherLogger.getInstance().writeLog(output);
    }
  }

  /**
   * Logs the provided message as an error message.
   *
   * @param message         The message to be logged.
   * @param isWrittenToFile Indicates whether the message should be written to a
   *                        file.
   */
  public static void logError(
      String message,
      boolean isWrittenToFile
  ) {
    if (isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.error(String.format(
          "%s: %s",
          LoggerHelper.getCallerMethodName(), message)
      );
    }

    if (isWrittenToFile) {
      String output = String.format(
          "[ERROR] %s - %s",
          LoggerHelper.getCallerClassName(), message
      );

      LauncherLogger.getInstance().writeLog(output);
    }
  }

  /**
   * Logs the provided message as an error message.
   *
   * @param message         The message to be logged.
   * @param t               The throwable that caused the error.
   * @param isWrittenToFile Indicates whether the message and stack trace should
   *                        be written to a file.
   */
  public static void logError(
      String message,
      Throwable t,
      boolean isWrittenToFile
  ) {
    Throwable cause = t.getCause() != null ? t.getCause() : t;
    if (isUsingDebugger) {
      Logger logger = LoggerFactory.getLogger(
          LoggerHelper.getCallerClassName());
      logger.error(String.format(
          "%s: %s",
          LoggerHelper.getCallerMethodName(), message), cause
      );
    }

    if (isWrittenToFile) {
      String output = String.format(
          "[ERROR] %s - %s",
          LoggerHelper.getCallerClassName(), message);

      LauncherLogger.getInstance().writeLog(output, cause);
    }
  }
}
