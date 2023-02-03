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
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;
import lombok.Getter;

public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final Image bgImage;

  @Getter
  private final LauncherLoginPanel launcherLoginPanel;

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    this.bgImage = FilesManager.readImageFile(LauncherPanel.class, "icon/dirt.png");
    this.setPreferredSize(new Dimension(854, 480));

    this.launcherLoginPanel = new LauncherLoginPanel();
    this.add(this.launcherLoginPanel);
  }

  private void drawTitleString(String s, int pWidth, int pHeight, Graphics2D g2d) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(s);
    int stringHeight = fm.getHeight();
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) - (stringHeight << 1);

    g2d.drawString(s, stringX, stringY);
  }

  public void showError(String error) {
    this.removeAll();

    this.add(this.launcherLoginPanel);
    this.launcherLoginPanel.getErrorLabel().setText(error);

    this.revalidate();
    this.repaint();
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
    VolatileImage compatibleVolatileImage = gc.createCompatibleVolatileImage(panelWidth >> 1,
        panelHeight >> 1, Transparency.TRANSLUCENT);

    Graphics2D g2d = compatibleVolatileImage.createGraphics();
    try {
      for (int gridIndex = 0; gridIndex < (gridWidth * gridHeight); gridIndex++) {
        int gridX = imageWidth * (gridIndex % gridWidth);
        int gridY = imageHeight * (gridIndex / gridWidth);
        g2d.drawImage(this.bgImage, gridX, gridY, imageWidth, imageHeight, this);
      }

      String title = "TwentyTen Launcher";
      this.drawTitleString(title, panelWidth, panelHeight, g2d);
    } finally {
      g2d.dispose();
    }

    g.drawImage(compatibleVolatileImage, 0, 0, panelWidth, panelHeight, 0, 0, panelWidth >> 1,
        panelHeight >> 1, this);
  }
}