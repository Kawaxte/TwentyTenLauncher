package ee.twentyten;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.discord.DiscordRichPresenceUtils;
import ee.twentyten.util.launcher.LauncherUtils;
import ee.twentyten.util.launcher.options.LanguageUtils;
import ee.twentyten.util.launcher.options.VersionUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Launcher {

  public static void main(String... args) {
    if (LauncherUtils.MIN_MEMORY > LauncherUtils.MAX_MEMORY) {
      LauncherUtils.buildAndCreateProcess();
    }

    ConfigUtils.readFromConfig();
    LanguageUtils.loadLocale();
    VersionUtils.getVersionsFile();

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        DiscordRichPresenceUtils.discordShutdown();
      }
    }));

    DiscordRichPresenceUtils.buildAndUpdateRichPresence("", "Idle");

    ScheduledExecutorService rpcService = Executors.newSingleThreadScheduledExecutor();
    rpcService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        DiscordRichPresenceUtils.discordRunCallbacks();
      }
    }, 0, 2, TimeUnit.SECONDS);

    LauncherFrame.main(args);
  }
}
