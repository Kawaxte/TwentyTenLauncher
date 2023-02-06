package ee.twentyten.ui;

import com.mojang.util.YggdrasilHelper;
import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.ConfigHelper;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LauncherHelper;
import ee.twentyten.util.LogHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
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
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.minecraft.MinecraftLauncher;
import org.json.JSONObject;


public class LauncherFrame extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static final Class<LauncherFrame> CLASS_REF;
  public static boolean sessionExpired;
  private static LauncherFrame instance;

  static {
    CLASS_REF = LauncherFrame.class;
  }

  private final LauncherLoginPanel loginPanel;
  private final LauncherOfflinePanel offlinePanel;
  private boolean loginRememberPassword;
  private LauncherPanel panel;

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
          LogHelper.logError(CLASS_REF, "Failed to launch default browser", ioe1);

          EPlatform platform = EPlatform.getPlatform();
          Objects.requireNonNull(platform, "platform");
          try {
            RuntimeHelper.executeCommand(platform, LauncherFrame.this.loginPanel.getLinkUrls());
          } catch (IOException ioe2) {
            LogHelper.logError(CLASS_REF, "Failed to execute string command", ioe2);
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
      LogHelper.logError(LauncherFrame.class, "Failed to get versions file", ioe);
      return;
    }

    LauncherConfig.instance = LauncherConfig.load();
    Objects.requireNonNull(LauncherConfig.instance, "config == null!");
    if (LauncherConfig.instance.getClientToken() == null || LauncherConfig.instance.getClientToken()
        .isEmpty()) {
      ConfigHelper.initConfig();
    }
    if (!LauncherConfig.instance.getAccessToken().isEmpty()
        && !LauncherConfig.instance.getClientToken().isEmpty()) {
      boolean session = YggdrasilHelper.isSessionExpired(LauncherConfig.instance.getAccessToken(),
          LauncherConfig.instance.getClientToken());
      System.out.println("Session: " + session);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(
            String.format("TwentyTen Launcher %s", LauncherHelper.CURRENT_VERSION));
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String loginUsername = this.loginPanel.getUsernameField().getText();
    String loginPassword = new String(this.loginPanel.getPasswordField().getPassword());
    this.loginRememberPassword = this.loginPanel.getRememberPasswordCheckBox().isSelected();

    Object source = event.getSource();
    if (source == LauncherFrame.this.loginPanel.getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(LauncherFrame.instance);
      optionsDialog.setVisible(true);
    }
    if (source == LauncherFrame.this.loginPanel.getLoginButton()) {
      if (!LauncherConfig.instance.getUsingBeta() && !LauncherConfig.instance.getUsingAlpha()
          && !LauncherConfig.instance.getUsingInfdev()) {
        JOptionPane.showMessageDialog(this,
            "You can't launch the game without selecting a version!", "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      boolean credentialsEmpty = loginUsername.isEmpty() || loginPassword.isEmpty();
      boolean credentialsChanged = !LauncherConfig.instance.getUsername().equals(loginUsername)
          || !LauncherConfig.instance.getPassword().equals(loginPassword);
      if (!LauncherFrame.sessionExpired || credentialsChanged || credentialsEmpty) {
        this.loginWithYggdrasil(loginUsername, loginPassword,
            LauncherConfig.instance.getClientToken());
      } else {
        this.loginWithSession();
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

  private void loginWithYggdrasil(String username, String password, String token) {
    JSONObject result = YggdrasilHelper.authenticate(username, password, token, true);
    if (result.has("error")) {
      LauncherFrame.this.panel.showError("Login failed");
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
    LauncherConfig.instance.save();
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
}
