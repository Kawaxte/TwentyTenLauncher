package ee.twentyten.launcher;

import ee.twentyten.launcher.ui.LauncherFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Launcher {

  private static final long MIN_MEMORY;
  private static final long MAX_MEMORY;

  static {
    MIN_MEMORY = 524288L;
    MAX_MEMORY = Runtime.getRuntime().maxMemory();
  }

  public static void main(String[] args) {
    List<String> arguments = new ArrayList<>();
    arguments.add(EPlatform.getPlatform() == EPlatform.WINDOWS ? "javaw" : "java");
    arguments.add("-Xmx1024m");
    arguments.add("-Dsun.java2d.d3d=false");
    arguments.add("-Dsun.java2d.opengl=false");
    arguments.add("-Dsun.java2d.pmoffscreen=false");
    arguments.add("-cp");
    arguments.add(System.getProperty("java.class.path"));
    arguments.add(LauncherFrame.class.getName());

    if (MAX_MEMORY < MIN_MEMORY) {
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
      } catch (InterruptedException ie) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while waiting for the process to terminate:%n%s",
                ie.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      LauncherFrame.main(args);
    }
  }
}
