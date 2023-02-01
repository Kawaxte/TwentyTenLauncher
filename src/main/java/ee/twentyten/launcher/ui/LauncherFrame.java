package ee.twentyten.launcher.ui;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.CommandsManager;
import ee.twentyten.util.ConfigManager;
import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.FilesManager;
import ee.twentyten.util.LauncherManager;
import ee.twentyten.util.OptionsManager;
import ee.twentyten.util.ThreadManager;
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
import javax.swing.SwingUtilities;
import net.minecraft.MinecraftLauncher;
import net.minecraft.util.AuthManager;
import org.json.JSONObject;


public class LauncherFrame extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static final int PRE_RELEASE_VERSION;
  private static final String CURRENT_VERSION;
  public static LauncherFrame instance;
  private static String configAccessToken;
  private static String configClientToken;
  private static String configUsername;
  private static String configPassword;

  static {
    PRE_RELEASE_VERSION = 1;
    CURRENT_VERSION = LauncherManager.getCurrentVersion(PRE_RELEASE_VERSION, true);
  }

  private final LauncherLoginPanel loginPanel;
  private String loginUsername;
  private String loginPassword;
  private boolean loginRememberPassword;
  private LauncherPanel panel;

  public LauncherFrame(String title) {
    super(title);

    this.setIconImage(FilesManager.readImageFile(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.loginPanel = panel.getLauncherLoginPanel();
    this.loginPanel.getOptionsButton().addActionListener(this);
    this.loginPanel.getLinkLabel().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        try {
          Desktop.getDesktop().browse(URI.create(loginPanel.getLinkUrls()));
        } catch (IOException ioe1) {
          DebugLoggingManager.logError(this.getClass(), "Failed to launch default browser", ioe1);

          EPlatform platform = EPlatform.getPlatform();
          Objects.requireNonNull(platform, "platform");
          try {
            CommandsManager.executeCommand(platform, loginPanel.getLinkUrls());
          } catch (IOException ioe2) {
            DebugLoggingManager.logError(this.getClass(), "Failed to execute string command", ioe2);
          }
        }
      }
    });
    this.loginPanel.getLoginButton().addActionListener(this);
    this.setContentPane(this.panel);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherManager.setLookAndFeel();
    try {
      OptionsManager.getVersionsFile();
    } catch (IOException ioe) {
      DebugLoggingManager.logError(LauncherFrame.class, "Failed to get versions file", ioe);
    }

    Config.instance = Config.load();
    if (Config.instance.getClientToken() == null) {
      ConfigManager.initConfig();
    }

    LauncherFrame.configClientToken = Config.instance.getClientToken();
    LauncherFrame.configAccessToken = Config.instance.getAccessToken();
    LauncherFrame.configUsername = Config.instance.getUsername();
    LauncherFrame.configPassword = Config.instance.getPassword();
    if (!LauncherFrame.configAccessToken.isEmpty() && !LauncherFrame.configClientToken.isEmpty()) {
      AuthManager.checkYggdrasilSession(LauncherFrame.configAccessToken,
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
      this.loginUsername = this.loginPanel.getUsernameField().getText();
      this.loginPassword = new String(this.loginPanel.getPasswordField().getPassword());
      this.loginRememberPassword = this.loginPanel.getRememberPasswordCheckBox().isSelected();
      
      final String[][] yggdrasilLogin = new String[1][2];
      final String[] profileName = new String[1];
      final String[] sessionId = new String[1];

      Thread loginThread = ThreadManager.createWorkerThread("LoginThread", new Runnable() {
        @Override
        public void run() {
          yggdrasilLogin[0] = LauncherFrame.this.loginWithYggdrasil();
          Objects.requireNonNull(yggdrasilLogin[0], "yggdrasilLogin == null!");
          profileName[0] = yggdrasilLogin[0][0];
          sessionId[0] = yggdrasilLogin[0][1];
        }
      });
      loginThread.start();
      try {
        loginThread.join();
      } catch (InterruptedException ie) {
        DebugLoggingManager.logError(this.getClass(), "Failed to wait for thread to die", ie);
      }
      if (yggdrasilLogin[0] != null) {
        this.launchMinecraft(profileName[0], sessionId[0]);
      }
    }
  }

  private String[] loginWithYggdrasil() {
    JSONObject authenticate = AuthManager.authenticateWithYggdrasil(this.loginUsername,
        this.loginPassword, LauncherFrame.configClientToken, true);
    if (authenticate.has("error")) {
      this.panel.showError("Login failed");
      return null;
    }

    String clientToken = authenticate.getString("clientToken");
    String accessToken = authenticate.getString("accessToken");
    Config.instance.setAccessToken(accessToken);
    Config.instance.setClientToken(clientToken);
    Config.instance.setUsername(this.loginUsername);
    Config.instance.setPassword(this.loginRememberPassword ? this.loginPassword : null);
    Config.instance.setPasswordSaved(this.loginRememberPassword);
    if (!this.loginUsername.equals(LauncherFrame.configUsername) || !this.loginPassword.equals(
        LauncherFrame.configPassword) || !this.loginRememberPassword) {
      Config.instance.save();
    }

    String profileUuid = authenticate.getJSONObject("selectedProfile").getString("id");
    String profileName = authenticate.getJSONObject("selectedProfile").getString("name");
    String sessionId = String.format("%s:%s:%s", clientToken, accessToken, profileUuid);
    return new String[]{profileName, sessionId};
  }

  private void launchMinecraft(String username, String sessionId) {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", username);
    launcher.parameters.put("sessionid", sessionId);
    launcher.init();

    this.getContentPane().removeAll();
    this.getContentPane().repaint();
    this.add(launcher);

    this.revalidate();
    this.repaint();

    launcher.start();
    this.panel = null;
    this.setTitle("Minecraft");
  }

  private void launchMinecraftOffline() {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", "Player");
    launcher.init();

    this.getContentPane().removeAll();
    this.getContentPane().repaint();
    this.add(launcher);

    this.revalidate();
    this.repaint();

    launcher.start();
    this.setTitle("Minecraft");
  }
}
