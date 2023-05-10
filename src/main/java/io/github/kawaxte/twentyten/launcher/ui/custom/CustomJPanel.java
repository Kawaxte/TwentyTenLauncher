package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JPanel;
import lombok.val;

/**
 * This class extends {@link JPanel} and paints it the same way Markus "Notch" Persson did in the
 * original Minecraft Launcher.
 *
 * @author Markus "Notch" Persson
 */
public class CustomJPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link CustomJPanel} with FlowLayout and the specified buffering strategy.
   *
   * @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory
   * @see JPanel#JPanel(boolean)
   */
  public CustomJPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  @Override
  public Insets getInsets() {
    return new Insets(4, 18, 4, 18);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    val g2d = (Graphics2D) g;
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
