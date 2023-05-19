/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.kawaxte.twentyten.launcher.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Transparency;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import lombok.val;

public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  public static LauncherPanel instance;

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    LauncherPanel.instance = this;
    this.setBackground(Color.BLACK);

    val gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    this.add(new YggdrasilAuthPanel(), gbc);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    val bgImageUrl =
        Optional.ofNullable(this.getClass().getClassLoader().getResource("dirt.png"))
            .orElseThrow(() -> new NullPointerException("bgImageUrl cannot be null"));
    val bgImage = this.getToolkit().getImage(bgImageUrl);
    int bgImageWidth = bgImage.getWidth(this) << 1;
    int bgImageheight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    val g2d = (Graphics2D) g;
    val deviceConfiguration = g2d.getDeviceConfiguration();

    val bufferedImage =
        deviceConfiguration.createCompatibleImage(
            panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    val g2dBuffered = bufferedImage.createGraphics();
    g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
    try {
      int gridWidth = (panelWidth + bgImageWidth) >> 5;
      int gridHeight = (panelHeight + bgImageheight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .parallel()
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                g2dBuffered.drawImage(bgImage, gridX, gridY, bgImageWidth, bgImageheight, this);
              });
      g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

      this.drawTitleString("TwentyTen Launcher", panelWidth, panelHeight, g2dBuffered);
    } finally {
      g2dBuffered.dispose();
    }

    g2d.drawImage(bufferedImage, 0, 0, panelWidth, panelHeight, this);
  }

  private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }
}
