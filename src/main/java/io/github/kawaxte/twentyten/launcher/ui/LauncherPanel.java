package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.game.EState;
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
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class LauncherPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  public static LauncherPanel instance;
  @Getter @Setter private String taskStateMessage;
  @Getter @Setter private String taskProgressMessage;
  @Getter @Setter private int taskState;
  @Getter @Setter private int taskProgress;
  @Getter @Setter private boolean updaterTaskStarted;
  @Getter @Setter private boolean updaterTaskFinished;
  @Getter @Setter private boolean updaterTaskErrored;

  {
    this.taskState = EState.INITIALISE.ordinal();
    this.taskStateMessage = EState.INITIALISE.getMessage();
    this.taskProgressMessage = "";
    this.taskProgress = 0;
  }

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

    val bgImageUrl =
        Optional.ofNullable(this.getClass().getClassLoader().getResource("dirt.png"))
            .orElseThrow(() -> new NullPointerException("bgImageUrl cannot be null"));
    val bgImage = this.getToolkit().getImage(bgImageUrl);
    int bgImageWidth = bgImage.getWidth(this) << 1;
    int bgImageheight = bgImage.getHeight(this) << 1;
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();

    val g2d = (Graphics2D) g;
    val deviceConfiguration = g2d.getDeviceConfiguration();

    val bufferedImage =
        deviceConfiguration.createCompatibleImage(
            panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
    val g2dBuffered = bufferedImage.createGraphics();
    g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
    try {
      int gridWidth = (panelWidth + bgImageWidth) >> 5;
      int gridHeight = (panelHeight + bgImageheight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .parallel()
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                g2dBuffered.drawImage(bgImage, gridX, gridY, bgImageWidth, bgImageheight, this);
              });
      g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

      if (!this.updaterTaskStarted) {
        this.drawTitleString("TwentyTen Launcher", panelWidth, panelHeight, g2dBuffered);
      } else {
        val title =
            this.updaterTaskErrored
                ? LauncherLanguage.bundle.getString("lp.updaterErrored")
                : LauncherLanguage.bundle.getString("lp.updaterStarted");
        this.drawTitleString(title, panelWidth, panelHeight, g2dBuffered);
        this.drawTaskStateString(this.taskStateMessage, panelWidth, panelHeight, g2dBuffered);

        if (!this.updaterTaskErrored) {
          this.drawTaskProgressString(
              this.taskProgressMessage, panelWidth, panelHeight, g2dBuffered);
          this.drawTaskProgressRect(panelWidth, panelHeight, g2dBuffered);
        }
      }
    } finally {
      g2dBuffered.dispose();
    }

    g2d.drawImage(bufferedImage, 0, 0, panelWidth, panelHeight, this);
  }

  private void drawTaskProgressRect(int width, int height, Graphics2D g2d) {
    int rectX = 64;
    int rectY = (height >> 1) - rectX;
    int rectWidth = (width >> 1) - 128;
    int rectHeight = 5;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(rectX, rectY, rectWidth + 1, rectHeight);

    g2d.setColor(Color.GREEN.darker().darker());
    g2d.fillRect(rectX, rectY, (this.taskProgress * (rectWidth)) / 100, rectHeight - 1);

    g2d.setColor(Color.GREEN.darker());
    g2d.fillRect(rectX, rectY + 1, ((this.taskProgress * rectWidth) / 100) - 2, rectHeight - 4);
  }

  private void drawTaskProgressString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.PLAIN, 12f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawTaskStateString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.PLAIN, 12f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }
}
