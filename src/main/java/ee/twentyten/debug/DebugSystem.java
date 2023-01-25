package ee.twentyten.debug;

import java.lang.management.ManagementFactory;

public final class DebugSystem {

  private static final boolean DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments()
      .toString().contains("-agentlib:jdwp");
  private static final String DEBUG_FORMAT = "[DEBUG] (%s::%s) %s%n";

  private DebugSystem() {
  }

  public static void printf(String format, Object... args) {
    if (DEBUG) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String className = element.getClassName();
      String methodName = element.getMethodName();
      System.out.printf(DEBUG_FORMAT, className, methodName,
          String.format(format, args));
    }
  }

  public static void println(Object obj) {
    if (DEBUG) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[2];
      String className = element.getClassName();
      String methodName = element.getMethodName();
      System.out.printf(DEBUG_FORMAT, className, methodName, obj);
    }
  }
}
