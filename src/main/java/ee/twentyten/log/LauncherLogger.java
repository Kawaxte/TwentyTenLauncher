package ee.twentyten.log;

import ee.twentyten.EPlatform;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.launcher.LauncherUtils;
import ee.twentyten.util.log.LoggerUtils;
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

  File getMinecraftLogFile() {
    EPlatform platform = EPlatform.getPlatform();

    StringBuilder sb = new StringBuilder();
    sb.append(MessageFormat.format("{0}_log-", ConfigUtils.getInstance().getSelectedVersion()));
    sb.append(LoggerUtils.getCurrentDateAndTime());
    sb.append(platform == EPlatform.WINDOWS ? ".txt" : ".log");

    File logsDirectory = new File(LauncherUtils.workingDirectory, "logs");
    if (!logsDirectory.exists() && !logsDirectory.mkdirs()) {
      LoggerUtils.logMessage("Failed to create logs directory", ELevel.ERROR);
      return null;
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
