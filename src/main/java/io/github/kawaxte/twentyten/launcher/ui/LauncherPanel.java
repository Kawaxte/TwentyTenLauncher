package io.github.kawaxte.twentyten.launcher.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Transparency;
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

    val image = this.getToolkit().getImage(getClass().getResource("/dirt.png"));
    int imageWidth = image.getWidth(this) << 1;
    int imageHeight = image.getHeight(this) << 1;

    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    val g2d = (Graphics2D) g;
    val g2dConfiguration = g2d.getDeviceConfiguration();

    val bufferedImage =
        g2dConfiguration.createCompatibleImage(
            panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    val bufferedImageG2d = bufferedImage.createGraphics();
    bufferedImageG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
    try {
      int gridWidth = (panelWidth + imageWidth) >> 5;
      int gridHeight = (panelHeight + imageHeight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .parallel()
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                bufferedImageG2d.drawImage(image, gridX, gridY, imageWidth, imageHeight, this);
              });
      bufferedImageG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));

      String title = "TwentyTen Launcher";
      bufferedImageG2d.setFont(this.getFont().deriveFont(Font.BOLD, 20F));
      bufferedImageG2d.setColor(Color.LIGHT_GRAY);

      val fm = bufferedImageG2d.getFontMetrics();
      int titleWidth = fm.stringWidth(title);
      int titleHeight = fm.getHeight();
      int titleX = (panelWidth >> 2) - (titleWidth >> 1);
      int titleY = (panelHeight >> 2) - (titleHeight << 1);
      bufferedImageG2d.drawString(title, titleX, titleY);
    } finally {
      bufferedImageG2d.dispose();
    }

    g2d.drawImage(bufferedImage, 0, 0, panelWidth, panelHeight, this);
  }
}
