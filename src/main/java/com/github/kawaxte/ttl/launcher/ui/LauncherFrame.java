package com.github.kawaxte.ttl.launcher.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public LauncherFrame() {
    super("TwentyTen Launcher");
    this.setContentPane(new JPanel(new GridBagLayout(), true) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Image bgImage = this.getToolkit().getImage(this.getClass()
            .getClassLoader().getResource("dirt.png"));

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
          int titleX = (frameWidth >> 1 >> 1) - (titleWidth >> 1);
          int titleY = (frameHeight >> 1 >> 1) - (titleHeight << 1);
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

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;

    switch (UIManager.getLookAndFeel().getClass().getCanonicalName()) {
      case "com.apple.laf.AquaLookAndFeel":
        gbc.insets.top = 29;
        break;
      case "com.sun.java.swing.plaf.motif.MotifLookAndFeel":
        gbc.insets.top = 31;
        break;
      case "javax.swing.plaf.metal.MetalLookAndFeel":
        gbc.insets.top = 11;
        break;
      case "javax.swing.plaf.nimbus.NimbusLookAndFeel":
        gbc.insets.top = 21;
        break;
      default:
        break;
    }
    this.getContentPane().add(new YggdrasilLoginPanel(), gbc);
    this.pack();

    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    this.getContentPane().setFont(font);
  }
}

