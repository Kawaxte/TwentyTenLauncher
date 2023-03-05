package ee.twentyten.ui.launcher;

import com.microsoft.util.MicrosoftUtils;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.component.TransparentJButton;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LookAndFeelUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

public class LauncherPanel extends JPanel implements ActionListener {

  @Getter
  @Setter
  private static LauncherPanel instance;
  @Getter
  private final TransparentJButton microsoftLoginButton;
  private final LauncherLoginPanel loginPanel;

  {
    this.loginPanel = new LauncherLoginPanel();

    this.microsoftLoginButton = new TransparentJButton(
        LanguageUtils.getString("lp.button.microsoftLoginButton"));

    this.microsoftLoginButton.addActionListener(this);
  }

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    LauncherPanel.setInstance(this);
    this.setPreferredSize(new Dimension(854, 480));

    this.buildPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.microsoftLoginButton,
        "lp.button.microsoftLoginButton");
  }

  private void buildPanel() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets.top = 16;
    this.add(this.loginPanel, gbc);

    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets.top = 0;
    this.add(this.microsoftLoginButton, gbc);
  }

  private void drawTitleString(Graphics2D g2d, String title, int width, int height) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(title, titleX, titleY);
  }

  @Override
  public void updateUI() {
    if (LookAndFeelUtils.isUsingWindowsClassicTheme != LookAndFeelUtils.isWindowsClassic()) {
      SwingUtilities.updateComponentTreeUI(this);
    }
    super.updateUI();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Image bgImage = FileUtils.readImageResource("icon/dirt.png", LauncherPanel.class);
    if (bgImage == null) {
      bgImage = this.createImage(1, 1);
    }

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    VolatileImage vImg = gc.createCompatibleVolatileImage(panelWidth >> 1, panelHeight >> 1,
        Transparency.OPAQUE);

    Graphics2D g2d = vImg.createGraphics();
    try {
      int bgWidth = bgImage.getWidth(this) << 1;
      int bgHeight = bgImage.getHeight(this) << 1;
      int gridWidth = (panelWidth + bgWidth) >> 5;
      int gridHeight = (panelHeight + bgHeight) >> 5;
      for (int i = 0; i < (gridWidth * gridHeight); i++) {
        int gridX = (i % gridWidth) << 5;
        int gridY = (i / gridWidth) << 5;
        g2d.drawImage(bgImage, gridX, gridY, bgWidth, bgHeight, this);
      }

      String title = "TwentyTen Launcher";
      this.drawTitleString(g2d, title, panelWidth, panelHeight);
    } finally {
      g2d.dispose();
    }
    g.drawImage(vImg, 0, 0, panelWidth, panelHeight, this);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source.equals(this.microsoftLoginButton)) {
      if (!LauncherUtils.isNetworkAvailableForMicrosoft()) {
        return;
      }
      if (LauncherUtils.isLauncherOutdated()) {
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.outdatedLauncher"));
        return;
      }
      MicrosoftUtils.loginWithMicrosoft();
    }
  }
}
