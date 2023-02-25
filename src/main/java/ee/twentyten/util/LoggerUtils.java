package ee.twentyten.util;

import ee.twentyten.log.ELogger;
import ee.twentyten.log.LauncherLoggerImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class LoggerUtils {

  public static boolean isDebugging;
  private static LauncherLoggerImpl logger;

  static {
    LoggerUtils.logger = new LauncherLoggerImpl();

    LoggerUtils.isDebugging = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
        .contains("-agentlib:jdwp");
  }

  private LoggerUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static char[] getCurrentTime() {
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    String formattedTime = sdf.format(currentTime);
    return formattedTime.toCharArray();
  }

  private static String getCallerClassName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    return elements[4].getClassName();
  }

  private static String getCallerMethodName() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    return elements[4].getMethodName();
  }

  public static String formatLogMessage(String message, ELogger type) {
    return new StringBuilder().append("[").append(LoggerUtils.getCurrentTime()).append("] [")
        .append(type).append("] ").append(LoggerUtils.getCallerClassName()).append("::")
        .append(LoggerUtils.getCallerMethodName()).append(" - ").append(message).toString();
  }

  public static String formatLogMessage(String message, Throwable t, ELogger type) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatLogMessage(message, type)).append(SystemUtils.lineSeparator);
    if (t != null) {
      try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
        t.printStackTrace(pw);
        sb.append(sw);
        sb.delete(sb.length() - SystemUtils.lineSeparator.length(), sb.length());
      } catch (IOException ioe) {
        sb.append(ioe.getMessage());
      }
    }
    return sb.toString();
  }

  public static void log(String message, ELogger type) {
    String logMessage = LoggerUtils.formatLogMessage(message, type);
    if (LoggerUtils.isDebugging) {
      switch (type) {
        case INFO:
        case WARN:
          System.out.println(logMessage);
          break;
        default:
          System.err.println(logMessage);
          break;
      }
    }
    LoggerUtils.logger.log(logMessage);
  }

  public static void log(String message, Throwable t, ELogger type) {
    String logMessage = LoggerUtils.formatLogMessage(message, t, type);
    if (LoggerUtils.isDebugging) {
      switch (type) {
        case INFO:
        case WARN:
          System.out.println(logMessage);
          break;
        default:
          System.err.println(logMessage);
          break;
      }
    }
    LoggerUtils.logger.log(logMessage);
  }
}
