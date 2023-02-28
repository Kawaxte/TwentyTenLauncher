package ee.twentyten.ui;

import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LookAndFeelUtils;
import ee.twentyten.util.SystemUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

public class LauncherFrame extends JFrame {
  
  @Getter
  @Setter
  public static LauncherFrame instance;

  public LauncherFrame() {
    super(MessageFormat.format("TwentyTen Launcher {0}", SystemUtils.launcherVersion));

    LauncherFrame.setInstance(this);
    this.setIconImage(FileUtils.readImageResource("icon/favicon.png", LauncherFrame.class));
    this.setMinimumSize(new Dimension(640, 480));

    this.getContentPane().setBackground(Color.BLACK);

    this.add(new LauncherPanel());
    this.pack();

    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(true);
    this.setVisible(true);
  }

  public static void main(String... args) {
    LookAndFeelUtils.setLookAndFeel();
    SystemUtils.setLauncherVersion(1, 28, 2, 23, 8, true);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LauncherFrame();
      }
    });
  }

  @Override
  public void update(Graphics g) {
    if (LookAndFeelUtils.isUsingWindowsClassicTheme != LookAndFeelUtils.isWindowsClassic()) {
      SwingUtilities.updateComponentTreeUI(this);
    }
    super.update(g);
  }
}
