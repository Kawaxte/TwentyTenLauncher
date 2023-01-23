package ee.twentyten.debug;

import java.lang.management.ManagementFactory;

public final class DebugSystem {

  private static final boolean debug = ManagementFactory.getRuntimeMXBean().getInputArguments()
      .toString().contains("-agentlib:jdwp");

  private DebugSystem() {
  }

  public static void printf(String format, Object... args) {
    if (debug) {
      String className = Thread.currentThread().getStackTrace()[2].getClassName();
      String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
      System.out.printf("[DEBUG] %s::%s %s", className, methodName, String.format(format, args));
    }
  }

  public static void println(Object obj) {
    if (debug) {
      String className = Thread.currentThread().getStackTrace()[2].getClassName();
      String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
      System.out.println("[DEBUG] " + className + "::" + methodName + " " + obj);
    }
  }
}
