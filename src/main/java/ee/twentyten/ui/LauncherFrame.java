package ee.twentyten.ui;

import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.launcher.ui.LookAndFeelUtils;
import ee.twentyten.util.minecraft.auth.AuthenticationUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.text.MessageFormat;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

public class LauncherFrame extends JFrame {

  @Getter
  @Setter
  private static LauncherFrame instance;

  public LauncherFrame() {
    super(MessageFormat.format("TwentyTen Launcher {0}", SystemUtils.launcherVersion));

    LauncherFrame.setInstance(this);
    this.setIconImage(FileUtils.readImageResource("icon/favicon.png", LauncherFrame.class));
    this.setMinimumSize(new Dimension(640, 480));

    this.getContentPane().setBackground(Color.BLACK);

    this.add(new LauncherPanel());
    this.pack();

    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.setResizable(true);
    this.setVisible(true);
  }

  public static void main(String... args) {
    LookAndFeelUtils.setLookAndFeel();
    SystemUtils.setLauncherVersion(1, 11, 3, 23, 4, true);

    if (AuthenticationUtils.isMicrosoftSessionValid(
        ConfigUtils.getInstance().getMicrosoftAccessToken(),
        ConfigUtils.getInstance().getMicrosoftRefreshToken())) {
      AuthenticationUtils.checkMinecraftTokenExpirationTime(
          ConfigUtils.getInstance().getMicrosoftRefreshToken(),
          ConfigUtils.getInstance().getMicrosoftAccessTokenExpiresIn());
    }
    if (AuthenticationUtils.isYggdrasilSessionValid(
        ConfigUtils.getInstance().getYggdrasilAccessToken())) {
      AuthenticationUtils.validateAndRefreshAccessToken(ConfigUtils.getInstance().getClientToken());
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LauncherFrame();
      }
    });
  }
}
