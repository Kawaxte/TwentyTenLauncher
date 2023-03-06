package ee.twentyten.util;

import ee.twentyten.log.ELevel;
import ee.twentyten.log.LauncherLoggerImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

public final class LoggerUtils {

  public static boolean isDebugging;
  @Getter
  @Setter
  private static LauncherLoggerImpl instance;

  static {
    LoggerUtils.setInstance(new LauncherLoggerImpl());

    LoggerUtils.isDebugging = ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
        .contains("-agentlib:jdwp");
  }

  private LoggerUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static char[] getCurrentTime() {
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    String formattedTime = sdf.format(currentTime);
    return formattedTime.toCharArray();
  }

  public static char[] getCurrentDateAndTime() {
    Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

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

  public static String formatLogMessage(String message, ELevel level) {
    return new StringBuilder().append("[").append(LoggerUtils.getCurrentTime()).append("] [")
        .append(level).append("] ").append(LoggerUtils.getCallerClassName()).append("::")
        .append(LoggerUtils.getCallerMethodName()).append(" - ").append(message).toString();
  }

  public static String formatLogMessage(String message, Throwable t, ELevel level) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatLogMessage(message, level)).append(SystemUtils.lineSeparator);
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

  public static void logMessage(String message, ELevel level) {
    String logMessage = LoggerUtils.formatLogMessage(message, level);
    if (LoggerUtils.isDebugging) {
      switch (level) {
        case INFO:
        case WARN:
          System.out.println(logMessage);
          break;
        default:
          System.err.println(logMessage);
          break;
      }
    }
    LoggerUtils.getInstance().log(logMessage);
  }

  public static void logMessage(String message, Throwable t, ELevel level) {
    String logMessage = LoggerUtils.formatLogMessage(message, t, level);
    if (LoggerUtils.isDebugging) {
      switch (level) {
        case INFO:
        case WARN:
          System.out.println(logMessage);
          break;
        default:
          System.err.println(logMessage);
          break;
      }
    }
    LoggerUtils.getInstance().log(logMessage);
  }

  public static void logPrintln() {
    LoggerUtils.getInstance().log();
  }
}
