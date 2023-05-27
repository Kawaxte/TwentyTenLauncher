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

package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JPanel;

/**
 * Class extending {@link javax.swing.JPanel} and overrides the {@code paintComponent...)} method to
 * provide a specific style of graphics to every new JPanel. This avoids the need to duplicate the
 * paint override method for each panel.
 *
 * <p>The graphics style provided by this class includes a layered rectangle design and is a refined
 * variant of the original code provided by Markus "Notch" Persson on Jun 29, 2010.
 *
 * <p>The {@code getInsets()} method is also overridden to provide custom insets (4, 18, 4, 18),
 * which are used to provide a margin around the panel contents in the exact same way as the
 * original code provided by Markus "Notch" Persson.
 *
 * @see javax.swing.JPanel
 * @author Kawaxte
 * @since 1.1.2323_01
 */
public class CustomJPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public CustomJPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  @Override
  public Insets getInsets() {
    return new Insets(4, 18, 4, 18);
  }

  /**
   * Creates a layered rectangle design using {@link java.awt.Graphics2D} drawing methods. The
   * design consists of:
   *
   * <ul>
   *   <li>An outer black rectangle with a stroke width of 2 pixels.
   *   <li>An inner white rectangle with a stroke width of 1 pixel.
   *   <li>A grey filled rectangle in the very centre.
   * </ul>
   *
   * @param g The {@link java.awt.Graphics} object to protect.
   * @see javax.swing.JComponent#paintComponent(Graphics)
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);

    g2d.setColor(Color.WHITE);
    g2d.setStroke(new BasicStroke(1));
    g2d.drawRect(2, 2, this.getWidth() - 5, this.getHeight() - 5);

    g2d.setColor(Color.GRAY);
    g2d.fillRect(3, 3, this.getWidth() - 6, this.getHeight() - 6);
  }
}
