package ee.twentyten.util;

import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingManager {

  public static final boolean DEBUGGING;

  static {
    DEBUGGING = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
        .contains("-agentlib:jdwp");
  }

  private LoggingManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void logInfo(Class<?> clazz, String message) {
    if (DEBUGGING) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String methodName = element.getMethodName();

      Logger logger = LoggerFactory.getLogger(clazz);
      logger.info(String.format("%s: %s", methodName, message));
    }
  }

  public static void logWarn(Class<?> clazz, String message) {
    if (DEBUGGING) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String methodName = element.getMethodName();

      Logger logger = LoggerFactory.getLogger(clazz);
      logger.warn(String.format("%s: %s", methodName, message));
    }
  }

  public static void logError(Class<?> clazz, String message, Throwable t) {
    t.printStackTrace();
    if (DEBUGGING) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String methodName = element.getMethodName();

      Logger logger = LoggerFactory.getLogger(clazz);
      logger.error(String.format("%s: %s", methodName, message), t);
    }
  }

  public static void logError(Class<?> clazz, String message) {
    if (DEBUGGING) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String methodName = element.getMethodName();

      Logger logger = LoggerFactory.getLogger(clazz);
      logger.error(String.format("%s: %s", methodName, message));
    }
  }
}
