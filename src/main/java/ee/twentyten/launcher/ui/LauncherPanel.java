package ee.twentyten.launcher.ui;

import ee.twentyten.util.FilesManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;
import lombok.Getter;

@Getter
public class LauncherPanel extends JPanel {

  private final Image bgImage;
  private final LauncherLoginPanel launcherLoginPanel;
  private VolatileImage gcVolatileBgImage;

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    this.bgImage = FilesManager.readImageFile(LauncherPanel.class, "icon/dirt.png");
    this.setPreferredSize(new Dimension(854, 480));

    this.launcherLoginPanel = new LauncherLoginPanel();
    this.add(this.launcherLoginPanel);
  }

  private void getTitle(int pWidth, int pHeight, Graphics2D g2d) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth("TwentyTen Launcher");
    int textHeight = fm.getHeight();

    g2d.drawString("TwentyTen Launcher", (pWidth >> 1 >> 1) - (textWidth >> 1),
        (pHeight >> 1 >> 1) - ((textHeight) << 1));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    int imageWidth = 32;
    int imageHeight = 32;
    int gridWidth = ((panelWidth + imageWidth) - 1) >> 5;
    int gridHeight = ((panelHeight + imageHeight) - 1) >> 5;

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    this.gcVolatileBgImage = gc.createCompatibleVolatileImage(panelWidth >> 1,
        panelHeight >> 1);

    Graphics2D g2d = gcVolatileBgImage.createGraphics();
    try {
      for (int index = 0; index < (gridWidth * gridHeight); index++) {
        int gridx = imageWidth * (index % gridWidth);
        int gridy = imageHeight * (index / gridWidth);
        g2d.drawImage(this.bgImage, gridx, gridy, imageWidth, imageHeight, this);
      }
      this.getTitle(panelWidth, panelHeight, g2d);
    } finally {
      g2d.dispose();
    }

    g.drawImage(this.gcVolatileBgImage, 0, 0, panelWidth, panelHeight, 0, 0, panelWidth >> 1,
        panelHeight >> 1, this);
  }
}