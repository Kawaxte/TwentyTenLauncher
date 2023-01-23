package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.utils.LookAndFeelManager;
import ee.twentyten.utils.VersionManager;
import javax.swing.SwingUtilities;

public class Launcher {

  public static void main(String[] args) {
    LookAndFeelManager.setLookAndFeel();

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        String version = VersionManager.getCurrentVersion();
        new LauncherFrame(String.format("TwentyTen Launcher %s", version));
      }
    });
  }
}
