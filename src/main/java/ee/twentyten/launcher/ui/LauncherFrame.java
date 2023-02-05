package ee.twentyten.launcher.ui;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.CommandManager;
import ee.twentyten.util.ConfigManager;
import ee.twentyten.util.FileManager;
import ee.twentyten.util.LauncherManager;
import ee.twentyten.util.LoggingManager;
import ee.twentyten.util.OptionsManager;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.minecraft.GameLauncher;
import net.minecraft.util.YggdrasilManager;
import org.json.JSONObject;


public class LauncherFrame extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static final int PRE_RELEASE_VERSION;
  private static final String CURRENT_VERSION;
  public static boolean sessionValid;
  private static LauncherFrame instance;
  private static String configAccessToken;
  private static String configClientToken;

  static {
    PRE_RELEASE_VERSION = 1;
    CURRENT_VERSION = LauncherManager.getCurrentVersion(PRE_RELEASE_VERSION, true);
  }

  private final LauncherLoginPanel loginPanel;
  private boolean loginRememberPassword;
  private LauncherPanel panel;

  public LauncherFrame(String title) {
    super(title);

    this.setIconImage(FileManager.readImageFile(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.loginPanel = this.panel.getLauncherLoginPanel();
    this.loginPanel.getOptionsButton().addActionListener(this);
    this.loginPanel.getLinkLabel().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        try {
          Desktop.getDesktop().browse(URI.create(LauncherFrame.this.loginPanel.getLinkUrls()));
        } catch (IOException ioe1) {
          LoggingManager.logError(this.getClass(), "Failed to launch default browser", ioe1);

          EPlatform platform = EPlatform.getPlatform();
          Objects.requireNonNull(platform, "platform");
          try {
            CommandManager.executeCommand(platform, LauncherFrame.this.loginPanel.getLinkUrls());
          } catch (IOException ioe2) {
            LoggingManager.logError(this.getClass(), "Failed to execute string command", ioe2);
          }
        }
      }
    });
    this.loginPanel.getLoginButton().addActionListener(this);
    this.add(this.panel, SwingConstants.CENTER);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherManager.setLookAndFeel();
    try {
      OptionsManager.downloadVersionsFile();
    } catch (IOException ioe) {
      LoggingManager.logError(LauncherFrame.class, "Failed to get versions file", ioe);
    }

    Config.instance = Config.load();
    Objects.requireNonNull(Config.instance, "config == null!");
    if (Config.instance.getClientToken() == null || Config.instance.getClientToken().isEmpty()) {
      ConfigManager.initConfig();
    }

    LauncherFrame.configClientToken = Config.instance.getClientToken();
    LauncherFrame.configAccessToken = Config.instance.getAccessToken();
    if (!LauncherFrame.configAccessToken.isEmpty() && !LauncherFrame.configClientToken.isEmpty()) {
      YggdrasilManager.checkYggdrasilSession(LauncherFrame.configAccessToken,
          LauncherFrame.configClientToken);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(
            java.lang.String.format("TwentyTen Launcher %s", CURRENT_VERSION));
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == LauncherFrame.this.loginPanel.getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(LauncherFrame.instance);
      optionsDialog.setVisible(true);
    }
    if (source == LauncherFrame.this.loginPanel.getLoginButton()) {
      String loginUsername = this.loginPanel.getUsernameField().getText();
      String loginPassword = new String(this.loginPanel.getPasswordField().getPassword());
      this.loginRememberPassword = this.loginPanel.getRememberPasswordCheckBox().isSelected();

      String savedUsername = Config.instance.getUsername();
      String savedPassword = Config.instance.getPassword();
      boolean credentialsEmpty = loginUsername.isEmpty() || loginPassword.isEmpty();
      boolean credentialsChanged = !savedUsername.equals(loginUsername)
          || !savedPassword.equals(loginPassword);
      if (!sessionValid || credentialsChanged || credentialsEmpty) {
        this.loginWithMojang(loginUsername, loginPassword,
            Config.instance.getClientToken());
      } else {
        this.loginWithSavedCredentials();
      }
    }
  }

  private void loginWithMojang(String username, String password, String token) {
    JSONObject result = YggdrasilManager.login(username, password, token,
        true);
    if (result.has("error")) {
      LauncherFrame.this.panel.showError("Login failed");
      return;
    }

    Config.instance.setUsername(username);
    Config.instance.setPassword(LauncherFrame.this.loginRememberPassword ? password : null);
    Config.instance.setPasswordSaved(LauncherFrame.this.loginRememberPassword);

    String accessToken = result.getString("accessToken");
    String clientToken = result.getString("clientToken");
    Config.instance.setAccessToken(accessToken);
    Config.instance.setClientToken(clientToken);

    String profileId;
    String profileName;
    if (!result.has("selectedProfile")) {
      profileId = "";
      profileName = "Player";
    } else {
      profileId = result.getJSONObject("selectedProfile").getString("id");
      profileName = result.getJSONObject("selectedProfile").getString("name");
    }
    Config.instance.setProfileId(profileId);
    Config.instance.setProfileName(profileName);
    Config.instance.save();

    this.loginWithSavedCredentials();
  }

  private void loginWithSavedCredentials() {
    String clientToken = Config.instance.getClientToken();
    String accessToken = Config.instance.getAccessToken();
    String profileId = Config.instance.getProfileId();
    String profileName = Config.instance.getProfileName();
    String sessionId = String.format("%s:%s:%s", clientToken, accessToken, profileId);
    String hasPaid = String.valueOf(!profileId.isEmpty() && !profileName.isEmpty());
    LauncherFrame.this.launchMinecraft(profileName, sessionId, hasPaid);
  }

  private void launchMinecraft(String name) {
    GameLauncher launcher = new GameLauncher();
    launcher.parameters.put("username", name);
    launcher.init();

    this.getContentPane().removeAll();
    this.add(launcher, SwingConstants.CENTER);

    this.revalidate();

    launcher.start();
    this.setTitle("Minecraft");
  }

  private void launchMinecraft(String username, String sessionId, String hasPaid) {
    GameLauncher launcher = new GameLauncher();
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
}
