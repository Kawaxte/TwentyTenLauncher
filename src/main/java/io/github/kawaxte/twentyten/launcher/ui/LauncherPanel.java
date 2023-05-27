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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.JPanel;

/**
 * Class representing the {@link javax.swing.JPanel} is the core panel for a {@link
 * javax.swing.JFrame}.
 *
 * @author Kawaxte
 * @since 1.3.2923_03
 */
public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private static LauncherPanel instance;

  /**
   * Constructor for LauncherPanel.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it.
   *
   * <p>Also, adds the {@link io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel} to the
   * current panel.
   *
   * @see #setLayout(LayoutManager)
   * @see #add(java.awt.Component, Object)
   */
  public LauncherPanel() {
    super(new GridBagLayout(), true);

    setInstance(this);

    this.setBackground(Color.BLACK);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    this.add(new YggdrasilAuthPanel(), gbc);
  }

  public static LauncherPanel getInstance() {
    return instance;
  }

  private static void setInstance(LauncherPanel lp) {
    instance = lp;
  }

  /**
   * Draws a 32x32 grid of the background image, and then draws the title string the same way Markus
   * "Notch" Persson did in the original Minecraft Launcher.
   *
   * <p>Instead of mainly using standard multiplication and division, this method uses bitwise
   * operations to achieve the same result, which is miles faster than the former.
   *
   * @param g the <code>Graphics</code> object to draw with
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    URL bgImageUrl =
        Optional.ofNullable(this.getClass().getClassLoader().getResource("dirt.png"))
            .orElseThrow(() -> new NullPointerException("bgImageUrl cannot be null"));
    Image bgImage = this.getToolkit().getImage(bgImageUrl);
    int bgImageWidth = bgImage.getWidth(this) << 1;
    int bgImageheight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    Graphics2D g2d = (Graphics2D) g;
    GraphicsConfiguration deviceConfiguration = g2d.getDeviceConfiguration();

    BufferedImage bufferedImage =
        deviceConfiguration.createCompatibleImage(
            panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    Graphics2D g2dBuffered = bufferedImage.createGraphics();
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

  /**
   * Draws the title string in the center of the panel.
   *
   * @param s the string to draw
   * @param width the width of the panel
   * @param height the height of the panel
   * @param g2d the <code>Graphics2D</code> object to draw with
   */
  private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }
}
