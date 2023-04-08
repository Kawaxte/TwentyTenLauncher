package com.github.kawaxte.twentyten.misc.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JPanel;
import lombok.var;

public class CustomJPanel extends JPanel {

  private static final long serialVersionUID = 1L;

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

    var g2d = (Graphics2D) g;
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
