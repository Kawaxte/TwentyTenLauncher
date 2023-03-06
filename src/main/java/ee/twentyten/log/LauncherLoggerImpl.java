package ee.twentyten.log;

import ee.twentyten.util.SystemUtils;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class LauncherLoggerImpl extends LauncherLogger {

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
  public void log() {
    try {
      PrintStream ps = new PrintStream(new FileOutputStream(this.getGameLogFile(), true));
      System.setOut(ps);
      System.setErr(ps);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
