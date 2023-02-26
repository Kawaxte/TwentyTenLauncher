package ee.twentyten.custom;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPanelUI;

public class TransparentPanelUI extends BasicPanelUI {

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    c.setOpaque(false);
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

    super.paint(g2d, c);
    g2d.dispose();
  }
}
