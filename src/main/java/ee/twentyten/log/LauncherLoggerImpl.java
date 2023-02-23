package ee.twentyten.log;

import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.SystemUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LauncherLoggerImpl extends LauncherLogger {

  private File getLogFile() {
    final File logFile = new File(LauncherUtils.workingDirectory, "twentyten_log.log");
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

  @Override
  public void log(String message) {
    try (FileWriter fw = new FileWriter(this.getLogFile(), true)) {
      fw.write(message);
      fw.write(SystemUtils.lineSeparator);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
