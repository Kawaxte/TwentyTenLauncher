package com.github.kawaxte.twentyten.log;

import com.github.kawaxte.twentyten.util.LauncherLoggerUtils;
import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;
import com.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class AbstractLauncherLoggerImpl extends AbstractLauncherLogger {

  public static final AbstractLauncherLoggerImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherLoggerImpl();
  }

  @Override
  public void log(ELevel level, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatMessage(level, message, args));
    if (LauncherUtils.DEBUG) {
      switch (level) {
        case WARNING:
        case ERROR:
          System.err.print(sb);
          break;
        default:
          System.out.print(sb);
          break;
      }
    }

    Path logFile = null;
    if (LauncherUtils.WORKING_DIRECTORY != null) {
      logFile = LauncherLoggerUtils.getLogFile(LauncherUtils.WORKING_DIRECTORY, "launcher_log");
    }
    if (logFile != null) {
      try (FileOutputStream fos = new FileOutputStream(logFile.toFile(), true)) {
        fos.write(sb.toString().getBytes());
        fos.flush();
      } catch (IOException ioe) {
        throw new RuntimeException("Could not write to log file", ioe);
      }
    }
  }

  @Override
  public void log(Throwable t, String message, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatMessage(ELevel.ERROR, message, args));
    if (t != null) {
      sb.append(t.getClass().getName());
      sb.append(":\u00A0");
      sb.append(t.getMessage()).append(System.lineSeparator());

      Arrays.stream(t.getStackTrace()).forEachOrdered(element -> {
        sb.append("\tat\u00A0");
        sb.append(element.toString()).append(System.lineSeparator());
      });
    }
    if (LauncherUtils.DEBUG) {
      System.err.print(sb);
    }

    Path logFile = null;
    if (LauncherUtils.WORKING_DIRECTORY != null) {
      logFile = LauncherLoggerUtils.getLogFile(LauncherUtils.WORKING_DIRECTORY, "launcher_log");
    }
    if (logFile != null) {
      try (FileOutputStream fos = new FileOutputStream(logFile.toFile(), true)) {
        fos.write(sb.toString().getBytes());
        fos.flush();
      } catch (IOException ioe) {
        throw new RuntimeException("Could not write to log file", ioe);
      }
    }
  }
}
