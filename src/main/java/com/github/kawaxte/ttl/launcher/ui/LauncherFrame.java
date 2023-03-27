package com.github.kawaxte.ttl.launcher.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public LauncherFrame() {
    super();

    this.setContentPane(new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 135), true) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Image bgImage = this.getToolkit().getImage(this.getClass().getClassLoader().getResource(
            "dirt.png"));

        int bgWidth = bgImage.getWidth(this) << 1;
        int bgHeight = bgImage.getHeight(this) << 1;
        int frameWidth = this.getWidth();
        int frameHeight = this.getHeight();
        VolatileImage vImage = g2d.getDeviceConfiguration().createCompatibleVolatileImage(
            frameWidth >> 1, frameHeight >> 1, Transparency.OPAQUE);

        Graphics2D vImageG2d = vImage.createGraphics();
        try {
          int gridWidth = (frameWidth + bgWidth) >> 5;
          int gridHeight = (frameHeight + bgHeight) >> 5;
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
          int titleX = (frameWidth >> 2) - (titleWidth >> 1);
          int titleY = (frameHeight >> 2) - (titleHeight << 1);
          vImageG2d.drawString(title, titleX, titleY);
        } finally {
          vImageG2d.dispose();
        }
        g2d.drawImage(vImage, 0, 0, frameWidth, frameHeight, this);
      }
    });

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(640, 480));
    this.setPreferredSize(new Dimension(854, 480));

    this.getContentPane().add(new YggdrasilLoginPanel());
    this.pack();

    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }

  @Override
  public String getTitle() {
    return "TwentyTen Launcher";
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    this.getContentPane().setFont(font);
  }
}

