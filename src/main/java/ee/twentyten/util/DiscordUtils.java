package ee.twentyten.util;

import ee.twentyten.EPlatform;
import ee.twentyten.discord.DiscordEventHandlers;
import ee.twentyten.discord.DiscordRichPresence;
import ee.twentyten.discord.DiscordUser;
import ee.twentyten.discord.IDiscordDynamicLib;
import ee.twentyten.discord.callback.IReadyCallback;
import ee.twentyten.log.ELevel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public final class DiscordUtils {

  @Getter
  @Setter
  private static DiscordRichPresence instance;

  static {
    DiscordUtils.loadLibraryForPlatform();
  }

  private DiscordUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void discordInitialise(String applicationId, DiscordEventHandlers handlers,
      boolean autoRegister, String optionalSteamId) {
    IDiscordDynamicLib.DISCORD_EXPORT.Discord_Initialize(applicationId, handlers,
        autoRegister ? 1 : 0, optionalSteamId);
  }

  public static void discordShutdown() {
    IDiscordDynamicLib.DISCORD_EXPORT.Discord_Shutdown();
  }

  public static void discordUpdatePresence(DiscordRichPresence presence) {
    IDiscordDynamicLib.DISCORD_EXPORT.Discord_UpdatePresence(presence);
  }

  public static void discordClearPresence() {
    IDiscordDynamicLib.DISCORD_EXPORT.Discord_ClearPresence();
  }

  public static void discordRunCallbacks() {
    IDiscordDynamicLib.DISCORD_EXPORT.Discord_RunCallbacks();
  }

  public static void updateRichPresence(String state) {
    if (DiscordUtils.getInstance() != null) {
      DiscordUtils.setInstance(new DiscordRichPresence.Builder(DiscordUtils.getInstance())
          .setState(state)
          .build());
      DiscordUtils.discordUpdatePresence(DiscordUtils.getInstance());
    }
  }

  public static void buildAndUpdateRichPresence(final String state) {
    DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(
        new IReadyCallback() {
          @Override
          public void ready(DiscordUser request) {
            LoggerUtils.logMessage(
                MessageFormat.format("{0}:{1}'#'{2}", request.userId, request.username,
                    request.discriminator), ELevel.INFO);

            DiscordUtils.setInstance(new DiscordRichPresence.Builder()
                .setState(state)
                .setStartTimestamp(System.currentTimeMillis() / 1000L)
                .setLargeImage("favicon_rpc", "https://github.com/Kawaxte/TwentyTenLauncher")
                .setSmallImage(SystemUtils.launcherVersion.contains("_pre")
                        ? "favicon_rpc_small_nightly" : "favicon_rpc_small_stable",
                    SystemUtils.launcherVersion)
                .build());
            DiscordUtils.discordUpdatePresence(DiscordUtils.getInstance());
          }
        }).build();
    DiscordUtils.discordInitialise("1058488125313253457", handlers, true, null);
  }

  public static void updateRichPresence(String state, String details) {
    if (DiscordUtils.getInstance() != null) {
      DiscordUtils.setInstance(new DiscordRichPresence.Builder(DiscordUtils.getInstance())
          .setState(state)
          .setDetails(details)
          .build());
      DiscordUtils.discordUpdatePresence(DiscordUtils.getInstance());
    }
  }

  private static void loadLibrary(Path p, URL url) {
    File libraryFile = p.toFile();
    try (InputStream in = url.openStream();
        OutputStream os = Files.newOutputStream(p)) {
      byte[] byteBuffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = in.read(byteBuffer)) != -1) {
        os.write(byteBuffer, 0, bytesRead);
      }
      libraryFile.deleteOnExit();
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to copy library file to temp directory", ioe, ELevel.ERROR);
    }
    System.load(libraryFile.getAbsolutePath());
  }

  private static void loadLibraryForPlatform() {
    String mappedLibraryName = System.mapLibraryName("discord-rpc");

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    File tempDirectory = null;
    Path tempLibraryPath = null;

    URL libraryFileUrl = null;
    switch (platform) {
      case MACOSX:
        tempDirectory = Paths.get(SystemUtils.userHome, "Library", "Application Support",
            "discord-rpc").toFile();
        tempLibraryPath = Paths.get(tempDirectory.getAbsolutePath(), mappedLibraryName);

        libraryFileUrl = DiscordUtils.class.getResource(
            MessageFormat.format("/rpc/amd64/{0}", mappedLibraryName));
        break;
      case LINUX:
        tempDirectory = Paths.get(SystemUtils.userHome, ".discord-rpc").toFile();
        tempLibraryPath = Paths.get(tempDirectory.getAbsolutePath(), mappedLibraryName);

        libraryFileUrl = DiscordUtils.class.getResource(
            MessageFormat.format("/rpc/amd64/{0}", mappedLibraryName));
        break;
      case WINDOWS:
        tempDirectory = Paths.get(SystemUtils.temp, "discord-rpc").toFile();
        tempLibraryPath = Paths.get(tempDirectory.getAbsolutePath(), mappedLibraryName);

        boolean isAMD64 =
            SystemUtils.sunArchDataModel.equals("64") && SystemUtils.osArch.equals("amd64");
        libraryFileUrl = DiscordUtils.class.getResource(
            MessageFormat.format("/rpc/{0}/{1}", isAMD64 ? "amd64" : "x86", mappedLibraryName));
        break;
      default:
        LoggerUtils.logMessage(String.valueOf(platform), ELevel.ERROR);
        break;
    }

    if (!tempDirectory.exists() && !tempDirectory.mkdirs()) {
      LoggerUtils.logMessage("Failed to create temp directory", ELevel.ERROR);
      return;
    }
    DiscordUtils.loadLibrary(tempLibraryPath, libraryFileUrl);
  }
}
