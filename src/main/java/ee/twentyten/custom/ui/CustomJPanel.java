package ee.twentyten.custom.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class CustomJPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public CustomJPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  @Override
  public Insets getInsets() {
    return new Insets(15, 30, 15, 30);
  }

  @Override
  public Color getBackground() {
    return Color.GRAY;
  }

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
  }
}
