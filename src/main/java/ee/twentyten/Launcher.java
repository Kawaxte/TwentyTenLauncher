package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.LogHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Launcher {

  private static final long MIN_MEMORY;
  private static final long MAX_MEMORY;
  private static final Class<Launcher> CLASS_REF;

  static {
    MIN_MEMORY = 524288L;
    MAX_MEMORY = Runtime.getRuntime().maxMemory();

    CLASS_REF = Launcher.class;
  }

  public static void main(String[] args) {
    if (MAX_MEMORY < MIN_MEMORY) {
      List<String> arguments = new ArrayList<>();
      arguments.add(EPlatform.getPlatform() == EPlatform.WINDOWS ? "javaw" : "java");
      arguments.add("-Xmx1024m");
      arguments.add("-Dsun.java2d.d3d=false");
      arguments.add("-Dsun.java2d.opengl=false");
      arguments.add("-Dsun.java2d.pmoffscreen=false");
      arguments.add("-cp");
      arguments.add(System.getProperty("java.class.path"));
      arguments.add(CLASS_REF.getName());

      ProcessBuilder pb = new ProcessBuilder(arguments);
      try {
        Process process = pb.start();
        if (process.waitFor() != 0) {
          System.exit(process.exitValue());
        }
      } catch (IOException ioe) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while starting the process:%n%s", ioe.getMessage()),
            "Error", JOptionPane.ERROR_MESSAGE);

        LogHelper.logError(CLASS_REF, "Failed to start the process", ioe);
      } catch (InterruptedException ie) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while waiting for the process to terminate:%n%s",
                ie.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);

        LogHelper.logError(CLASS_REF, "Failed to wait for the process to terminate",
            ie);
      }
    } else {
      LauncherFrame.main(args);
    }
  }
}
