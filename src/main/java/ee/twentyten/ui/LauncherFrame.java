package ee.twentyten.ui;

import ee.twentyten.utils.ImageManager;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

public class LauncherFrame extends JFrame {

  LauncherPanel panel;

  public LauncherFrame(String title) {
    super(title);
    try {
      this.setIconImage(ImageManager.readImage(LauncherFrame.class, "icons/favicon.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.getContentPane().add(this.panel, SwingConstants.CENTER);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }
}
