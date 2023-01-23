package ee.twentyten;

import ee.twentyten.core.EPlatform;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.utils.ConfigManager;
import ee.twentyten.utils.LookAndFeelManager;
import ee.twentyten.utils.VersionManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Launcher {

  private static final String VERSION = VersionManager.getCurrentVersion();

  public static void main(String[] args)
      throws URISyntaxException, IOException {
    if (Runtime.getRuntime().maxMemory() < 512L * 1024L * 1024L) {
      System.out.print("Starting with process");
      String jarPath = Launcher.class.getProtectionDomain().getCodeSource()
          .getLocation().toURI()
          .getPath();

      List<String> arguments = new LinkedList<>();
      arguments.add(EPlatform.getByOSNames() == EPlatform.WINDOWS ? "javaw" : "java");
      arguments.add("-Xmx1G");
      arguments.add("-Dsun.java2d.opengl=false");
      arguments.add("-Dsun.java2d.d3d=false");
      arguments.add("-Dsun.java2d.pmoffscreen=false");
      arguments.add("-cp");
      arguments.add(jarPath);
      arguments.add("ee.twentyten.launcher.Launcher");

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
    }

    LookAndFeelManager.setLookAndFeel();

    LauncherConfig config = LauncherConfig.load();
    if (config.getClientToken() == null) {
      try {
        ConfigManager.initConfig();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException("Can't initialise the config!", e);
      }
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LauncherFrame(String.format("TwentyTen Launcher %s", VERSION));
      }
    });
  }
}
