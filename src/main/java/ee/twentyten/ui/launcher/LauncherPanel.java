package ee.twentyten.ui.launcher;

import com.microsoft.util.MicrosoftHelper;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LoggerHelper;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.Getter;

public class LauncherPanel extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final Image bgImage;
  private final String microsoftLoginButtonText;
  @Getter
  private LauncherLoginPanel loginPanel;
  @Getter
  private JButton microsoftLoginButton;

  {
    this.bgImage = FileHelper.readImageFile(LauncherPanel.class,
        "icon/dirt.png");

    this.microsoftLoginButtonText = LauncherLanguage.getString(
        "llp.button.microsoftLoginButton");
  }

  public LauncherPanel() {
    super(new GridBagLayout(), true);

    Dimension panelSize = new Dimension(854, 480);
    this.setPreferredSize(panelSize);

    GridBagConstraints gbc = new GridBagConstraints();
    this.addComponents(gbc);

    this.microsoftLoginButton.addActionListener(this);
  }

  private void addComponents(GridBagConstraints gbc) {
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets.top = 23;
    this.loginPanel = new LauncherLoginPanel();
    this.add(this.loginPanel, gbc);

    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets.top = 0;

    this.microsoftLoginButton = new JButton(this.microsoftLoginButtonText);
    this.add(this.microsoftLoginButton, gbc);
  }

  public void addNoNetworkPanel(String message) {
    this.removeAll();

    LauncherNoNetworkPanel noNetworkPanel = new LauncherNoNetworkPanel();
    noNetworkPanel.getErrorLabel().setText(message);
    this.add(noNetworkPanel);

    this.revalidate();
    this.repaint();
  }

  public void addMicrosoftLoginPanel(String userCode,
      int expiresIn, String verificationUri) {
    this.removeAll();

    LauncherMicrosoftLoginPanel microsoftLoginPanel = new LauncherMicrosoftLoginPanel();
    microsoftLoginPanel.getUserCodeLabel().setText(userCode);
    microsoftLoginPanel.setExpiresIn(expiresIn);
    microsoftLoginPanel.setVerificationUri(verificationUri);
    microsoftLoginPanel.startProgressBar();
    this.add(microsoftLoginPanel);

    this.revalidate();
    this.repaint();
  }

  private void drawTitleString(String title, int pWidth, int pHeight,
      Graphics2D g2d) {
    Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    g2d.setFont(titleFont);
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(title);
    int stringHeight = fm.getHeight();
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) - (stringHeight << 1);

    g2d.drawString(title, stringX, stringY);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    int imageWidth = 32;
    int imageHeight = 32;
    int gridWidth = ((panelWidth + imageWidth) - 1) >> 5;
    int gridHeight = ((panelHeight + imageHeight) - 1) >> 5;

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    VolatileImage compatVolatileImage = gc.createCompatibleVolatileImage(
        panelWidth >> 1, panelHeight >> 1, Transparency.TRANSLUCENT);

    Graphics2D g2d = compatVolatileImage.createGraphics();
    try {
      for (int gridIndex = 0; gridIndex < (gridWidth * gridHeight);
          gridIndex++) {
        int gridX = imageWidth * (gridIndex % gridWidth);
        int gridY = imageHeight * (gridIndex / gridWidth);

        g2d.drawImage(this.bgImage, gridX, gridY, imageWidth, imageHeight,
            this);
      }

      String title = "TwentyTen Launcher";
      this.drawTitleString(title, panelWidth, panelHeight, g2d);
    } finally {
      g2d.dispose();
    }

    g.drawImage(compatVolatileImage, 0, 0, panelWidth, panelHeight, 0, 0,
        panelWidth >> 1, panelHeight >> 1, this);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.microsoftLoginButton) {
      /*
      if (this.loginPanel.isLauncherOutdated(this)) {
        return;
      }
      */
      if (LauncherLoginPanel.isVersionSelected()) {
        return;
      }

      try {
        InetAddress address = InetAddress.getByName("www.minecraft.net");
        if (address != null) {
          String refreshToken = LauncherConfig.instance.getRefreshToken();
          if (refreshToken.isEmpty() || LauncherFrame.isSessionExpired) {
            MicrosoftHelper.authenticate(this);
          }
        }
        
        if (!LauncherConfig.instance.getRefreshToken().isEmpty()) {
          String profileName = LauncherConfig.instance.getProfileName();
          String sessionId = LauncherConfig.instance.getSessionId();
          LauncherFrame.instance.launchMinecraft(profileName, sessionId, true);
        }
      } catch (UnknownHostException uhe) {
        this.addNoNetworkPanel(this.loginPanel.getErrorLabelOutdatedText());

        LoggerHelper.logError("Failed to get address", uhe, true);
      }
    }
  }
}