package ee.twentyten.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class CustomJPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new {@code CustomJPanel} with the specified layout manager and
   * buffer state.
   *
   * @param layout           the layout manager to use
   * @param isDoubleBuffered a boolean, true for double-buffered, which uses
   *                         additional memory space to achieve fast,
   *                         flicker-free updates
   */
  public CustomJPanel(
      LayoutManager layout,
      boolean isDoubleBuffered
  ) {
    super(layout, isDoubleBuffered);

    /* Set the background color of the panel. */
    this.setBackground(Color.GRAY);
  }

  /**
   * Returns the insets (i.e. the margins) of the component.
   *
   * @return the insets of the component
   */
  @Override
  public Insets getInsets() {
    return new Insets(14, 30, 21, 30);
  }

  /**
   * The `paintComponent` method is used to draw graphics on a component.
   *
   * @param g The Graphics object to draw on.
   */
  @Override
  public void paintComponent(
      Graphics g
  ) {
    super.paintComponent(g);

    /* Cast the Graphics object to Graphics2D. */
    Graphics2D g2d = (Graphics2D) g;

    /* Set the color to black. */
    g2d.setColor(Color.BLACK);

    /* Set the stroke to 2 pixels wide. */
    g2d.setStroke(new BasicStroke(2));

    /* Draw a rectangle with the specified width and height. */
    g2d.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);

    /* Set the color to white. */
    g2d.setColor(Color.WHITE);

    /* Set the stroke to 1 pixel wide. */
    g2d.setStroke(new BasicStroke(1));

    /* Draw a rectangle with the specified width and height. */
    g2d.drawRect(2, 2, this.getWidth() - 5, this.getHeight() - 5);
  }
}
