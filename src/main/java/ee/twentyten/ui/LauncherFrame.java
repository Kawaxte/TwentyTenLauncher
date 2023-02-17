package ee.twentyten.ui;

import com.mojang.util.YggdrasilHelper;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigHelper;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LauncherHelper;
import ee.twentyten.util.LauncherVersionHelper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.minecraft.MinecraftLauncher;


public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  public static LauncherFrame instance;
  public static String launcherVersion;
  public static String launcherTitle;
  public static boolean isSessionExpired;

  static {
    LauncherVersionHelper.getCurrentVersion(1, 2, 17, 23, true, 1);
    LauncherFrame.launcherVersion = System.getProperty("ee.twentyten.version");
    LauncherFrame.launcherTitle = String.format("TwentyTen Launcher %s",
        LauncherFrame.launcherVersion);
  }

  @Getter
  private final LauncherPanel panel;

  public LauncherFrame(String title) {
    super(title);

    this.setIconImage(
        FileHelper.readImageFile(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.add(this.panel, SwingConstants.CENTER);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherHelper.setLookAndFeel();

    LauncherConfig.instance = LauncherConfig.loadConfig();
    Objects.requireNonNull(LauncherConfig.instance,
        "LauncherConfig.instance == null!");

    String clientToken = LauncherConfig.instance.getClientToken();
    String accessToken = LauncherConfig.instance.getAccessToken();
    String refreshToken = LauncherConfig.instance.getRefreshToken();
    if (clientToken == null || clientToken.length() < 32) {
      ConfigHelper.initConfig();
    }
    if (!accessToken.isEmpty() && refreshToken.isEmpty()) {
      YggdrasilHelper.validateAndRefresh(accessToken, clientToken, true);
    }
    if (!accessToken.isEmpty() && !refreshToken.isEmpty()) {
      //MicrosoftHelper.refresh(refreshToken);
    }

    String selectedLanguage = LauncherConfig.instance.getSelectedLanguage();
    LauncherLanguage.setLanguage(selectedLanguage);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(LauncherFrame.launcherTitle);
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  public void launchMinecraft(boolean hasPaid) {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", "Player");
    launcher.parameters.put("haspaid", String.valueOf(hasPaid));
    launcher.init();

    this.getContentPane().removeAll();
    this.add(launcher, BorderLayout.CENTER);
    this.validate();

    launcher.start();
    this.setTitle("Minecraft");
  }

  public void launchMinecraft(String username, String sessionId,
      boolean hasPaid) {
    MinecraftLauncher launcher = new MinecraftLauncher();
    launcher.parameters.put("username", username);
    launcher.parameters.put("sessionid", sessionId);
    launcher.parameters.put("haspaid", String.valueOf(hasPaid));
    launcher.init();

    this.getContentPane().removeAll();
    this.add(launcher, BorderLayout.CENTER);
    this.validate();

    launcher.start();
    this.setTitle("Minecraft");
  }
}
