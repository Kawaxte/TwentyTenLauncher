package ee.twentyten.log;

import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.SystemUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;

public class LauncherLoggerImpl extends LauncherLogger {

  private File getLogFile() {
    File logFile = new File(LauncherUtils.workingDirectory, "twentyten_log.log");
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

  private File getMinecraftLogFile() {
    StringBuilder sb = new StringBuilder();
    sb.append(MessageFormat.format("{0}_", ConfigUtils.getInstance().getSelectedVersion()));
    sb.append(LoggerUtils.getCurrentDateAndTime());
    sb.append(".log");

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

  @Override
  public void log(String message) {
    try (FileWriter fw = new FileWriter(this.getLogFile(), true)) {
      fw.write(message);
      fw.write(SystemUtils.lineSeparator);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  @Override
  public void logMinecraft() {
    try {
      PrintStream ps = new PrintStream(new FileOutputStream(this.getMinecraftLogFile(), true));
      System.setOut(ps);
      System.setErr(ps);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
