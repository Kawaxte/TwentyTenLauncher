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
    List<String> parameters = new ArrayList<>();
    parameters.add(EPlatform.getPlatform() == EPlatform.WINDOWS ? "javaw" : "java");
    parameters.add("-Xmx1G");
    parameters.add("-Dsun.java2d.d3d=false");
    parameters.add("-Dsun.java2d.opengl=false");
    parameters.add("-Dsun.java2d.pmoffscreen=false");
    parameters.add("-cp");
    parameters.add(System.getProperty("java.class.path"));
    parameters.add(LauncherFrame.class.getName());

    if (EUnit.convert(MAX_MEMORY, EUnit.MEGABYTE) < MIN_MEMORY) {
      ProcessBuilder pb = new ProcessBuilder(parameters);
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
