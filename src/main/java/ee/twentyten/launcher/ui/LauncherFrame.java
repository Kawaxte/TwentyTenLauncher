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

  static {
    PRE_RELEASE_VERSION = 1;
    CURRENT_VERSION = LauncherManager.getCurrentVersion(PRE_RELEASE_VERSION, true);
  }

  private final LauncherLoginPanel loginPanel;
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
      public void mouseClicked(MouseEvent ae) {
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
    LauncherManager.getWorkingDirectoryForPlatform();
    LauncherManager.setLookAndFeel();

    Config.instance = Config.load();
    if (Config.instance.getClientToken() == null) {
      ConfigManager.initConfig();
    }

    try {
      OptionsManager.getVersionsFile();
    } catch (IOException ioe) {
      DebugLoggingManager.logError(LauncherFrame.class, "Failed to get versions file", ioe);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(
            String.format("TwentyTen Launcher %s", CURRENT_VERSION));
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == loginPanel.getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(LauncherFrame.instance);
      optionsDialog.setVisible(true);
    }
    if (source == loginPanel.getLoginButton()) {
      final String loginUsername = loginPanel.getUsernameField().getText();
      final String loginPassword = new String(loginPanel.getPasswordField().getPassword());
      final boolean loginRememberPasswordSelected = loginPanel.getRememberPasswordCheckBox()
          .isSelected();
      String loginClientToken = Config.instance.getClientToken();

      final JSONObject result = AuthManager.authenticateWithYggdrasil(loginUsername, loginPassword,
          loginClientToken, true);
      if (result.has("error")) {
        System.out.print("yes");
        return;
      }

      ThreadManager.createWorkerThread("LoginThread", new Runnable() {
        @Override
        public void run() {
          loginWithYggdrasil(loginUsername, loginPassword, loginRememberPasswordSelected, result);
        }
      }).start();
    }
  }

  private void loginWithYggdrasil(String username, String password, boolean rememberPassword,
      JSONObject data) {
    String name = data.getJSONObject("selectedProfile").getString("name");
    String uuid = data.getJSONObject("selectedProfile").getString("id");
    String clientToken = data.getString("clientToken");
    String accessToken = data.getString("accessToken");
    String configUsername = Config.instance.getUsername();
    String configPassword = Config.instance.getPassword();
    String sessionId = String.format("%s:%s:%s", clientToken, accessToken, uuid);

    Config.instance.setAccessToken(accessToken);
    Config.instance.setClientToken(clientToken);
    Config.instance.setUsername(username);
    Config.instance.setPassword(rememberPassword ? password : null);
    Config.instance.setPasswordSaved(rememberPassword);
    if (!username.equals(configUsername) || !password.equals(configPassword) || !rememberPassword) {
      Config.instance.save();
    }
    this.launchMinecraft(name, sessionId);
  }

  private void launchMinecraft(String name, String sessionId) {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", name);
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
