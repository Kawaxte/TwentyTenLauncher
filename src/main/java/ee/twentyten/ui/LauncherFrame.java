package ee.twentyten.ui;

import ee.twentyten.minecraft.ui.MinecraftWrapper;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.AuthenticationUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.DiscordUtils;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LookAndFeelUtils;
import ee.twentyten.util.SystemUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

public class LauncherFrame extends JFrame implements WindowFocusListener {

  @Getter
  @Setter
  private static LauncherFrame instance;
  private ScheduledExecutorService idleService;

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

    this.addWindowFocusListener(this);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        if (LauncherFrame.this.idleService != null) {
          LauncherFrame.this.idleService.shutdown();
          LauncherFrame.this.idleService = null;
        }
        DiscordUtils.discordClearPresence();
        DiscordUtils.discordShutdown();
      }
    });
  }

  public static void main(String... args) {
    LookAndFeelUtils.setLookAndFeel();
    SystemUtils.setLauncherVersion(1, 13, 3, 23, 5, false);

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

    DiscordUtils.buildAndUpdateRichPresence("Idle");

    ScheduledExecutorService callbackService = Executors.newSingleThreadScheduledExecutor();
    callbackService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        DiscordUtils.discordRunCallbacks();
      }
    }, 2, 2, TimeUnit.SECONDS);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LauncherFrame();
      }
    });
  }

  @Override
  public void windowGainedFocus(WindowEvent event) {
    if (this.idleService != null) {
      this.idleService.shutdown();
      this.idleService = null;
    }
    DiscordUtils.updateRichPresence(
        MinecraftWrapper.getInstance() != null && MinecraftWrapper.getInstance()
            .getMinecraftApplet().isActive() ? "Playing Minecraft" : "Idle");
  }

  @Override
  public void windowLostFocus(WindowEvent event) {
    this.idleService = Executors.newSingleThreadScheduledExecutor();
    this.idleService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        if (!LauncherFrame.this.isActive()) {
          DiscordUtils.updateRichPresence("AFK");
        }
      }
    }, 5, 2, TimeUnit.MINUTES);
  }
}
