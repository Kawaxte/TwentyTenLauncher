package com.github.kawaxte.twentyten.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.net.URL;
import java.util.stream.IntStream;
import javax.swing.JPanel;

public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    this.add(new YggdrasilLoginPanel(), gbc);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;

    URL bgImageUrl = this.getClass().getClassLoader().getResource("dirt.png");
    Image bgImage = this.getToolkit().getImage(bgImageUrl);

    int bgWidth = bgImage.getWidth(this) << 1;
    int bgHeight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    VolatileImage vImage = g2d.getDeviceConfiguration().createCompatibleVolatileImage(
        panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);

    Graphics2D vImageG2d = vImage.createGraphics();
    try {
      int gridWidth = (panelWidth + bgWidth) >> 5;
      int gridHeight = (panelHeight + bgHeight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight)).parallel().forEach(i -> {
        int gridX = (i % gridWidth) << 5;
        int gridY = (i / gridWidth) << 5;
        vImageG2d.drawImage(bgImage, gridX, gridY, bgWidth, bgHeight, this);
      });

      String title = "TwentyTen Launcher";
      vImageG2d.setFont(this.getFont().deriveFont(Font.BOLD, 20F));
      vImageG2d.setColor(Color.LIGHT_GRAY);

      FontMetrics fm = vImageG2d.getFontMetrics();
      int titleWidth = fm.stringWidth(title);
      int titleHeight = fm.getHeight();
      int titleX = (panelWidth >> 2) - (titleWidth >> 1);
      int titleY = (panelHeight >> 2) - (titleHeight << 1);
      vImageG2d.drawString(title, titleX, titleY);

      /* DEBUG
      vImageG2d.setColor(Color.RED);
      vImageG2d.drawLine(0, titleY - 1, titleX + panelWidth, titleY - 1);

      vImageG2d.setColor(Color.GREEN);
      vImageG2d.drawLine(0, titleY + 10, titleX + panelWidth, titleY + 10);
       */
    } finally {
      vImageG2d.dispose();
    }
    g2d.drawImage(vImage, 0, 0, panelWidth, panelHeight, this);
  }
}
