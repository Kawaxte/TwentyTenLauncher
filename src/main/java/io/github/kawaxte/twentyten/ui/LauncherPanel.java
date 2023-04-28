package io.github.kawaxte.twentyten.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Transparency;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import lombok.val;

public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    this.setBackground(Color.BLACK);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    this.add(new YggdrasilLoginPanel(), gbc);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    val g2d = (Graphics2D) g;
    val bgImageUrl =
        Optional.ofNullable(LauncherPanel.class.getClassLoader().getResource("dirt.png"))
            .orElseThrow(() -> new RuntimeException("Failed to load background image"));
    val bgImage = this.getToolkit().getImage(bgImageUrl);

    int bgWidth = bgImage.getWidth(this) << 1;
    int bgHeight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    val image =
        g2d.getDeviceConfiguration()
            .createCompatibleImage(panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    val imageG2d = image.createGraphics();
    imageG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));

    try {
      int gridWidth = (panelWidth + bgWidth) >> 5;
      int gridHeight = (panelHeight + bgHeight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .parallel()
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                imageG2d.drawImage(bgImage, gridX, gridY, bgWidth, bgHeight, this);
              });
      imageG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));

      String title = "TwentyTen Launcher";
      imageG2d.setFont(this.getFont().deriveFont(Font.BOLD, 20F));
      imageG2d.setColor(Color.LIGHT_GRAY);

      val imageG2dFontMetrics = imageG2d.getFontMetrics();
      int titleWidth = imageG2dFontMetrics.stringWidth(title);
      int titleHeight = imageG2dFontMetrics.getHeight();
      int titleX = (panelWidth >> 2) - (titleWidth >> 1);
      int titleY = (panelHeight >> 2) - (titleHeight << 1);
      imageG2d.drawString(title, titleX, titleY);
    } finally {
      imageG2d.dispose();
    }
    g2d.drawImage(image, 0, 0, panelWidth, panelHeight, this);
  }
}
