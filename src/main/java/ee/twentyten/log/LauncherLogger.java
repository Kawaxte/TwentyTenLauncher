package ee.twentyten.log;

import ee.twentyten.EPlatform;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

abstract class LauncherLogger {

  File getLogFile() {
    EPlatform platform = EPlatform.getPlatform();

    StringBuilder sb = new StringBuilder();
    sb.append("twentyten_log");
    sb.append(platform == EPlatform.WINDOWS ? ".txt" : ".log");

    File logFile = new File(LauncherUtils.workingDirectory, sb.toString());
    if (!logFile.exists()) {
      try {
        boolean isLogFileCreated = logFile.createNewFile();
        if (!isLogFileCreated) {
          throw new IOException("Failed to create log file");
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
    logFile.deleteOnExit();
    return logFile;
  }

  File getGameLogFile() {
    EPlatform platform = EPlatform.getPlatform();

    StringBuilder sb = new StringBuilder();
    sb.append(MessageFormat.format("{0}_log-", ConfigUtils.getInstance().getSelectedVersion()));
    sb.append(LoggerUtils.getCurrentDateAndTime());
    sb.append(platform == EPlatform.WINDOWS ? ".txt" : ".log");

    File logsDirectory = new File(LauncherUtils.workingDirectory, "logs");
    if (!logsDirectory.exists() && !logsDirectory.mkdirs()) {
      throw new RuntimeException("Failed to create logs directory");
    }
    File minecraftLogFile = new File(logsDirectory, sb.toString());
    if (!minecraftLogFile.exists()) {
      try {
        boolean isLogFileCreated = minecraftLogFile.createNewFile();
        if (!isLogFileCreated) {
          throw new IOException("Failed to create Minecraft log file");
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
    return minecraftLogFile;
  }

  public abstract void log(String message);

  public abstract void log();
}
