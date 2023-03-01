package net.minecraft.util;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELoggerLevel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.VersionUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MinecraftLauncher;

public final class MinecraftUtils {

  public static URL lwjglUrl;
  public static URL minecraftJarUrl;
  public static String[] lwjglJars;
  public static String[] lwjglMacosxNatives;
  public static String[] lwjglLinuxNatives;
  public static String[] lwjglWindowsNatives;
  @Getter
  @Setter
  private static MinecraftLauncher instance;

  static {
    MinecraftUtils.setInstance(new MinecraftLauncher());

    MinecraftUtils.lwjglJars = new String[]{"jinput.jar", "lwjgl.jar", "lwjgl_util.jar"};
    MinecraftUtils.lwjglMacosxNatives = new String[]{"libinput_osx.jnilib", "liblwjgl.jnilib",
        "openal.dylib"};
    MinecraftUtils.lwjglLinuxNatives = new String[]{"libjinput_linux.so", "libjinput_linux64.so",
        "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so"};
    MinecraftUtils.lwjglWindowsNatives = new String[]{"jinput-dx8.dll", "jinput-dx8_64.dll",
        "jinput-raw.dll", "jinput-raw_64.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll",
        "OpenAL64.dll"};

    try {
      MinecraftUtils.lwjglUrl = new URL("https://archive.org/download/lwjgl-2/lwjgl-2.6");
      MinecraftUtils.minecraftJarUrl = new URL("https://archive.org/download/mc-legacy");
    } catch (MalformedURLException murle) {
      LoggerUtils.log("Failed to create URL", murle, ELoggerLevel.ERROR);
    }
  }

  private MinecraftUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static boolean isMinecraftCached() {
    EPlatform platform = EPlatform.getPlatform();

    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    Objects.requireNonNull(binDirectory, "binDirectory == null!");
    for (String lwjglFile : MinecraftUtils.lwjglJars) {
      if (!Files.exists(Paths.get(String.valueOf(binDirectory), lwjglFile))) {
        return false;
      }
    }

    File nativesDirectory = new File(binDirectory, "natives");
    Objects.requireNonNull(nativesDirectory, "nativesDirectory == null!");
    for (String nativeFile : platform == EPlatform.MACOSX ? MinecraftUtils.lwjglMacosxNatives
        : platform == EPlatform.LINUX ? MinecraftUtils.lwjglLinuxNatives
            : MinecraftUtils.lwjglWindowsNatives) {
      if (!Files.exists(Paths.get(String.valueOf(nativesDirectory), nativeFile))) {
        return false;
      }
    }

    File versionsDirectory = new File(LauncherUtils.workingDirectory, "versions");
    Objects.requireNonNull(versionsDirectory, "versionsDirectory == null!");
    if (!Files.exists(Paths.get(String.valueOf(versionsDirectory),
        ConfigUtils.getInstance().getSelectedVersion()))) {
      return false;
    }
    return Files.exists(
        Paths.get(String.valueOf(versionsDirectory), ConfigUtils.getInstance().getSelectedVersion(),
            MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion())));
  }

  public static void checkForLwjglJarFiles(List<URL> urls, URL url) {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File lwjglJarFile = new File(binDirectory, FileUtils.getFileName(url));
    if (!lwjglJarFile.exists()) {
      urls.add(url);
    }
  }

  public static void checkForLwjglNativeLibraryFiles(List<URL> urls, URL url, String[] libraries) {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    for (String library : libraries) {
      File lwjglNativeLibraryFile = new File(nativesDirectory, library);
      if (!lwjglNativeLibraryFile.exists()) {
        urls.add(url);
        break;
      }
    }
  }

  public static void checkForLwjglNativeLibraryFiles(EPlatform platform, List<URL> urls, URL url) {
    switch (platform) {
      case MACOSX:
        MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
            MinecraftUtils.lwjglMacosxNatives);
        break;
      case LINUX:
        MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url, MinecraftUtils.lwjglLinuxNatives);
        break;
      case WINDOWS:
        MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
            MinecraftUtils.lwjglWindowsNatives);
        break;
      default:
        throw new IllegalStateException(String.valueOf(platform));
    }
  }

  public static void checkForMinecraftJarFile(List<URL> urls, URL url) {
    File versionDirectory = new File(VersionUtils.versionsDirectory,
        ConfigUtils.getInstance().getSelectedVersion());
    File minecraftJarFile = new File(versionDirectory,
        MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion()));
    if (!minecraftJarFile.exists()) {
      urls.add(url);
    }
  }

  public static void launchMinecraft() {
    MinecraftUtils.getInstance().launch();
  }

  public static void launchMinecraft(String username, String sessionId) {
    MinecraftUtils.getInstance().launch(username, sessionId);
  }
}
