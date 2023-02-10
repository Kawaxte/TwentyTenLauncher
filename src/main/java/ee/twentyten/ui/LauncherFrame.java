package ee.twentyten.ui;

import com.mojang.util.YggdrasilHelper;
import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.ui.launcher.LauncherOfflinePanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigHelper;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LanguageHelper;
import ee.twentyten.util.LauncherHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.minecraft.MinecraftLauncher;
import org.json.JSONObject;


public class LauncherFrame extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  public static String version;
  public static LauncherFrame instance;
  private static boolean sessionExpired;

  static {
    LauncherHelper.getCurrentVersion(1, 2, 10, 23, true, 1);
    LauncherFrame.version = System.getProperty("ee.twentyten.version");
  }

  private final LauncherLoginPanel loginPanel;
  private final LauncherOfflinePanel offlinePanel;
  private final String errorLabelFailedText;
  private final String errorLabelOutdatedText;
  private final String errorLabelOfflineText;
  private final String optionsTitleText;
  private final String optionPaneTitleErrorText;
  private final String optionPaneErrorNoVersionText;
  private LauncherPanel panel;
  private boolean loginRememberPassword;

  {
    this.errorLabelFailedText = LanguageHelper.getString("lf.label.errorLabel.failed.text");
    this.errorLabelOutdatedText = LanguageHelper.getString("lf.label.errorLabel.outdated.text");
    this.errorLabelOfflineText = LanguageHelper.getString("lf.label.errorLabel.offline.text");
    this.optionsTitleText = LanguageHelper.getString("od.string.title.text");
    this.optionPaneTitleErrorText = LanguageHelper.getString("lf.optionpane.title.error.text");
    this.optionPaneErrorNoVersionText = LanguageHelper.getString(
        "lf.optionpane.error.noVersion.text");
  }

  public LauncherFrame(String title) {
    super(title);

    this.setIconImage(FileHelper.readImageFile(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.add(this.panel, SwingConstants.CENTER);

    this.loginPanel = this.panel.getLoginPanel();
    this.loginPanel.getOptionsButton().addActionListener(this);
    this.loginPanel.getLinkLabel().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        try {
          Desktop.getDesktop().browse(URI.create(LauncherFrame.this.loginPanel.getLinkUrls()));
        } catch (IOException ioe1) {
          LoggerHelper.logError("Failed to launch default browser", ioe1, true);

          EPlatform platform = EPlatform.getPlatform();
          Objects.requireNonNull(platform, "platform == null!");
          try {
            RuntimeHelper.executeUrl(platform, LauncherFrame.this.loginPanel.getLinkUrls());
          } catch (IOException ioe2) {
            LoggerHelper.logError("Failed to execute string command", ioe2, true);
          }
        }
      }
    });
    this.loginPanel.getLoginButton().addActionListener(this);

    this.offlinePanel = this.panel.getOfflinePanel();
    this.offlinePanel.getPlayOfflineButton().addActionListener(this);
    this.offlinePanel.getTryAgainButton().addActionListener(this);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherHelper.setLookAndFeel();
    try {
      OptionsHelper.downloadVersionsFile();
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to get versions file", ioe, true);
    }

    LauncherConfig.instance = LauncherConfig.loadConfig();
    Objects.requireNonNull(LauncherConfig.instance, "config == null!");

    boolean tokensEmpty = !LauncherConfig.instance.getAccessToken().isEmpty()
        && !LauncherConfig.instance.getClientToken().isEmpty();
    if (LauncherConfig.instance.getClientToken() == null || LauncherConfig.instance.getClientToken()
        .isEmpty()) {
      ConfigHelper.initConfig();
    }
    if (tokensEmpty) {
      LauncherFrame.sessionExpired = YggdrasilHelper.isSessionExpired(
          LauncherConfig.instance.getAccessToken(), LauncherConfig.instance.getClientToken());
    }

    LanguageHelper.setLanguage(LauncherConfig.instance.getSelectedLanguage());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(String.format("TwentyTen Launcher %s", version));
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  private void loginWithYggdrasil(String username, String password, String token) {
    JSONObject result = YggdrasilHelper.authenticate(username, password, token, true);
    if (result.has("error")) {
      LauncherFrame.this.panel.showError(this.errorLabelFailedText);
      return;
    }

    LauncherConfig.instance.setUsername(username);
    LauncherConfig.instance.setPassword(LauncherFrame.this.loginRememberPassword ? password : null);
    LauncherConfig.instance.setPasswordSaved(LauncherFrame.this.loginRememberPassword);

    String clientToken = result.getString("clientToken");
    String accessToken = result.getString("accessToken");
    String profileId;
    String profileName;
    if (!result.has("selectedProfile")) {
      profileId = "";
      profileName = "Player";
    } else {
      profileId = result.getJSONObject("selectedProfile").getString("id");
      profileName = result.getJSONObject("selectedProfile").getString("name");
    }

    LauncherConfig.instance.setClientToken(clientToken);
    LauncherConfig.instance.setAccessToken(accessToken);
    LauncherConfig.instance.setProfileId(profileId);
    LauncherConfig.instance.setProfileName(profileName);
    LauncherConfig.instance.saveConfig();
    this.loginWithSession();
  }

  private void loginWithSession() {
    String sessionId = String.format("%s:%s:%s", LauncherConfig.instance.getClientToken(),
        LauncherConfig.instance.getAccessToken(), LauncherConfig.instance.getProfileId());
    String hasPaid = String.valueOf(!LauncherConfig.instance.getProfileId().isEmpty()
        && !LauncherConfig.instance.getProfileName().isEmpty());

    LauncherFrame.this.launchMinecraft(LauncherConfig.instance.getProfileName(), sessionId,
        hasPaid);
  }

  private void launchMinecraft() {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", "Player");
    launcher.init();

    this.getContentPane().removeAll();
    this.add(launcher, SwingConstants.CENTER);

    this.revalidate();

    launcher.start();
    this.panel = null;

    this.setTitle("Minecraft");
  }

  private void launchMinecraft(String username, String sessionId, String hasPaid) {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", username);
    launcher.parameters.put("sessionid", sessionId);
    launcher.parameters.put("haspaid", hasPaid);
    launcher.init();

    this.getContentPane().removeAll();
    this.add(launcher, SwingConstants.CENTER);

    this.revalidate();

    launcher.start();
    this.panel = null;

    this.setTitle("Minecraft");
  }

  @Override
  public void dispose() {
    super.dispose();
    
    LauncherFrame.main(null);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String loginUsername = this.loginPanel.getUsernameField().getText();
    String loginPassword = new String(this.loginPanel.getPasswordField().getPassword());
    this.loginRememberPassword = this.loginPanel.getRememberPasswordCheckBox().isSelected();

    Object source = event.getSource();
    if (source == LauncherFrame.this.loginPanel.getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(this.optionsTitleText,
          LauncherFrame.instance);
      optionsDialog.setVisible(true);
    }
    if (source == LauncherFrame.this.loginPanel.getLoginButton()) {
      if (!LauncherConfig.instance.getUsingBeta() && !LauncherConfig.instance.getUsingAlpha()
          && !LauncherConfig.instance.getUsingInfdev()) {
        JOptionPane.showMessageDialog(this, this.optionPaneErrorNoVersionText,
            this.optionPaneTitleErrorText, JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        /*
        if (this.loginPanel.isOutdated()) {
          this.panel.showError(this.errorLabelOutdatedText);
          return;
        }
        */
        InetAddress address = InetAddress.getByName("minecraft.net");
        if (address != null) {
          boolean credentialsEmpty = loginUsername.isEmpty() || loginPassword.isEmpty();
          boolean credentialsChanged = !LauncherConfig.instance.getUsername().equals(loginUsername)
              || !LauncherConfig.instance.getPassword().equals(loginPassword);
          if (!LauncherFrame.sessionExpired || credentialsChanged || credentialsEmpty) {
            this.loginWithYggdrasil(loginUsername, loginPassword,
                LauncherConfig.instance.getClientToken());
          }
          this.loginWithSession();
        }
        return;
      } catch (IOException uhe) {
        this.panel.showNoNetwork(this.errorLabelOfflineText);
        return;
      }
    }

    if (source == LauncherFrame.this.offlinePanel.getPlayOfflineButton()) {
      this.launchMinecraft();
    }
    if (source == LauncherFrame.this.offlinePanel.getTryAgainButton()) {
      this.panel.removeAll();

      this.panel.add(this.loginPanel, SwingConstants.CENTER);

      this.panel.revalidate();
      this.panel.repaint();
    }
  }
}
