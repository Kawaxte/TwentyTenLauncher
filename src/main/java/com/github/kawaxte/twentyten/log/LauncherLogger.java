package com.github.kawaxte.twentyten.log;

import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;

public final class LauncherLogger {

  private LauncherLogger() {
  }

  public static void log(ELevel level, String message, Object... args) {
    AbstractLauncherLoggerImpl.INSTANCE.log(level, message, args);
  }

  public static void log(Throwable t, String message, Object... args) {
    AbstractLauncherLoggerImpl.INSTANCE.log(t, message, args);
  }
}
