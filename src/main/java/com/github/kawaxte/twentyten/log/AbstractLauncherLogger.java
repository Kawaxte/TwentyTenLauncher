package com.github.kawaxte.twentyten.log;

import com.github.kawaxte.twentyten.util.LauncherLoggerUtils;
import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;
import java.text.MessageFormat;

public abstract class AbstractLauncherLogger {

  String formatMessage(ELevel level, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(LauncherLoggerUtils.getTimestamp());
    sb.append("\u00A0");
    sb.append(level);
    sb.append("]\u00A0");
    sb.append(LauncherLoggerUtils.getCallerClassName());
    sb.append("::");
    sb.append(LauncherLoggerUtils.getCallerMethodName());
    sb.append("(");
    sb.append(LauncherLoggerUtils.getCallerLineNumber());
    sb.append(")\u00A0");
    sb.append("-\u00A0");
    return args.length == 0
        ? sb.append(message).append(System.lineSeparator()).toString()
        : sb.append(MessageFormat.format(message, args)).append(System.lineSeparator()).toString();
  }

  public abstract void log(ELevel level, String message, Object... args);

  public abstract void log(Throwable t, String message, Object... args);
}
