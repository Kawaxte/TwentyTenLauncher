package ee.twentyten.ui;

import ee.twentyten.LauncherConfig;
import ee.twentyten.core.ELookAndFeel;
import ee.twentyten.utils.ConfigManager;
import ee.twentyten.utils.ImageManager;
import ee.twentyten.utils.VersionManager;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public LauncherFrame() {
    super(String.format("TwentyTen Launcher %s", VersionManager.getCurrentVersion()));
    try {
      this.setIconImage(ImageManager.readImage(LauncherFrame.class, "icons/favicon.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.setMinimumSize(new Dimension(640, 480));

    LauncherPanel panel = new LauncherPanel();
    this.getContentPane().add(panel, SwingConstants.CENTER);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }

  public static void main(String[] args) {
    ELookAndFeel.setLookAndFeel();
    try {
      LauncherConfig config = LauncherConfig.load();
      if (config.getClientToken() == null) {
        ConfigManager.initConfig();
      }
    } catch (IOException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LauncherFrame();
      }
    });
  }
}
