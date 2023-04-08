package com.github.kawaxte.twentyten.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Transparency;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import lombok.var;

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

    var g2d = (Graphics2D) g;
    var bgImageUrl = this.getClass().getClassLoader().getResource("dirt.png");
    var bgImage = this.getToolkit().getImage(bgImageUrl);

    int bgWidth = bgImage.getWidth(this) << 1;
    int bgHeight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    var vImage = g2d.getDeviceConfiguration()
        .createCompatibleVolatileImage(panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    var vImageG2d = vImage.createGraphics();

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

      var vImageG2dFontMetrics = vImageG2d.getFontMetrics();
      int titleWidth = vImageG2dFontMetrics.stringWidth(title);
      int titleHeight = vImageG2dFontMetrics.getHeight();
      int titleX = (panelWidth >> 2) - (titleWidth >> 1);
      int titleY = (panelHeight >> 2) - (titleHeight << 1);
      vImageG2d.drawString(title, titleX, titleY);
    } finally {
      vImageG2d.dispose();
    }
    g2d.drawImage(vImage, 0, 0, panelWidth, panelHeight, this);
  }
}
