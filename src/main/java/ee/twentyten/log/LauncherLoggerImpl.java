package ee.twentyten.log;

import ee.twentyten.util.SystemUtils;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;

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
      PrintStream ps = new PrintStream(new FileOutputStream(this.getMinecraftLogFile(), true)) {
        @Override
        public void println(String x) {
          if (x != null && x.startsWith("Setting user: ")) {
            String[] parts = x.split(", ");
            if (parts.length == 2) {
              x = MessageFormat.format("Setting user: {0}, [REDACTED]", parts[0]);
            }
          }
          super.println(x);
        }
      };
      System.setOut(ps);
      System.setErr(ps);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
