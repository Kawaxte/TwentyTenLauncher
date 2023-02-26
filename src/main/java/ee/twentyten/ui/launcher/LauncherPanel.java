package ee.twentyten.ui.launcher;

import ee.twentyten.custom.TransparentJButton;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class LauncherPanel extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  public static LauncherPanel instance;
  private final BufferedImage bgImage;
  private final String panelTitle;
  @Getter
  private final TransparentJButton microsoftLoginButton;
  private final LauncherLoginPanel loginPanel;
  private final LauncherMicrosoftLoginPanel microsoftLoginPanel;

  {
    this.loginPanel = new LauncherLoginPanel();
    this.microsoftLoginPanel = new LauncherMicrosoftLoginPanel();

    this.bgImage = FileUtils.readImageResource("icon/dirt.png", LauncherPanel.class);
    this.panelTitle = "TwentyTen Launcher";
    this.microsoftLoginButton = new TransparentJButton(
        LanguageUtils.getString(LanguageUtils.microsoftLoginButtonKey));

    this.microsoftLoginButton.addActionListener(this);
  }

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    LauncherPanel.setInstance(this);
    this.setPreferredSize(new Dimension(854, 480));

    this.buildPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void show(Component comp) {
    this.removeAll();

    this.add(comp);

    this.revalidate();
    this.repaint();
  }

  public void showNoNetworkPanel(String message) {
    this.removeAll();

    this.add(new LauncherNoNetworkPanel());
    this.loginPanel.getErrorLabel().setText(message);

    this.revalidate();
    this.repaint();
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.microsoftLoginButton,
        LanguageUtils.microsoftLoginButtonKey);
  }

  private void buildPanel() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets.top = 23;
    this.add(this.loginPanel, gbc);

    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets.top = 0;
    this.add(this.microsoftLoginButton, gbc);
  }

  private void drawTitleString(Graphics g2d, String title, int width, int height) {
    Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    g2d.setFont(titleFont);
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fontMetrics = g2d.getFontMetrics();
    int titleWidth = fontMetrics.stringWidth(title);
    int titleHeight = fontMetrics.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(title, titleX, titleY);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    VolatileImage vImg = gc.createCompatibleVolatileImage(panelWidth >> 1, panelHeight >> 1,
        Transparency.OPAQUE);

    Graphics2D g2d = vImg.createGraphics();
    try {
      int bgWidth = this.bgImage.getWidth() << 1;
      int bgHeight = this.bgImage.getHeight() << 1;
      int gridWidth = (panelWidth + bgWidth) >> 5;
      int gridHeight = (panelHeight + bgHeight) >> 5;
      for (int grid = 0; grid < (gridWidth * gridHeight); grid++) {
        int gridX = (grid % gridWidth) << 5;
        int gridY = (grid / gridWidth) << 5;
        g2d.drawImage(this.bgImage, gridX, gridY, bgWidth, bgHeight, this);
      }

      this.drawTitleString(g2d, this.panelTitle, panelWidth, panelHeight);
    } finally {
      g2d.dispose();
    }
    g.drawImage(vImg, 0, 0, panelWidth, panelHeight, this);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.microsoftLoginButton) {
      if (LauncherUtils.isLauncherOutdated()) {
        LauncherPanel.getInstance().showNoNetworkPanel(
            LanguageUtils.getString(LanguageUtils.getBundle(), LanguageUtils.outdatedLauncherKey));
        return;
      }
      this.show(this.microsoftLoginPanel);
    }
  }
}
