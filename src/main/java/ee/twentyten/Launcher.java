package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.RuntimeHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Launcher {

  public static void main(String[] args) {
    if (RuntimeHelper.MAX_MEMORY < RuntimeHelper.MIN_MEMORY) {
      List<String> arguments = new ArrayList<>();
      arguments.add(EPlatform.getPlatform() == EPlatform.WINDOWS ? "javaw" : "java");
      arguments.add("-Xmx1024m");
      arguments.add("-Dsun.java2d.d3d=false");
      arguments.add("-Dsun.java2d.opengl=false");
      arguments.add("-Dsun.java2d.pmoffscreen=false");
      arguments.add("-cp");
      arguments.add(System.getProperty("java.class.path"));
      arguments.add(String.valueOf(Launcher.class));

      ProcessBuilder pb = new ProcessBuilder(arguments);
      try {
        Process process = pb.start();

        LoggerHelper.logInfo(pb.command().toString(), true);
        if (process.waitFor() != 0) {
          System.exit(process.exitValue());
        }
      } catch (IOException ioe) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while starting the process:%n%s", ioe.getMessage()),
            "Error", JOptionPane.ERROR_MESSAGE);

        LoggerHelper.logError("Failed to start the process", ioe, true);
      } catch (InterruptedException ie) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while waiting for the process to terminate:%n%s",
                ie.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);

        LoggerHelper.logError("Failed to wait for process to terminate", ie, true);
      }
    } else {
      LauncherFrame.main(args);
    }
  }
}
