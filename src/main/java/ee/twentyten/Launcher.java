package ee.twentyten;

import ee.twentyten.core.EPlatform;
import ee.twentyten.core.EUnit;
import ee.twentyten.ui.LauncherFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Launcher {

  private static final double MIN_MEMORY = 5.12e8;
  private static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();

  public static void main(String... args) {
    List<String> arguments = new ArrayList<>();
    arguments.add(EPlatform.getPlatform() == EPlatform.WINDOWS ? "javaw" : "java");
    arguments.add("-Xmx1024m");
    arguments.add("-Dsun.java2d.d3d=false");
    arguments.add("-Dsun.java2d.opengl=false");
    arguments.add("-Dsun.java2d.pmoffscreen=false");
    arguments.add("-cp");
    arguments.add(System.getProperty("java.class.path"));
    arguments.add(LauncherFrame.class.getName());

    if (EUnit.convert(MAX_MEMORY, EUnit.MEGABYTE) < MIN_MEMORY) {
      ProcessBuilder pb = new ProcessBuilder(arguments);
      try {
        Process process = pb.start();
        if (process.waitFor() != 0) {
          System.exit(process.exitValue());
        }
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while starting the process: %s%n", e.getMessage()),
            "Error", JOptionPane.ERROR_MESSAGE);
      } catch (InterruptedException e) {
        JOptionPane.showMessageDialog(null,
            String.format("An error occurred while waiting for the process to terminate: %s%n",
                e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      LauncherFrame.main(args);
    }
  }
}
