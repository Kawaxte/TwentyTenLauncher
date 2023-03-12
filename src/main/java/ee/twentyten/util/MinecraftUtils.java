package ee.twentyten.util;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELevel;
import ee.twentyten.minecraft.MinecraftLauncherImpl;
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

public final class MinecraftUtils {

  public static URL lwjglUrl;
  public static URL minecraftJarUrl;
  public static String[] lwjglJars;

  // Mac OS X (AARCH64 & AMD64)
  public static String[] lwjglMacosxNativesForAARCH64;
  public static String[] lwjglMacosxNativesForAMD64;

  // Linux (AARCH64 & AMD64 & AARCH32 & X86)
  public static String[] LwjglLinuxNativesForAARCH64;
  public static String[] lwjglLinuxNativesForAMD64;
  public static String[] lwjglLinuxNativesForAARCH32;
  public static String[] lwjglLinuxNativesForX86;

  // Windows (AMD64 & X86)
  public static String[] lwjglWindowsNativesForAMD64;
  public static String[] lwjglWindowsNativesForX86;
  @Getter
  @Setter
  private static MinecraftLauncherImpl instance;

  static {
    MinecraftUtils.setInstance(new MinecraftLauncherImpl());

    MinecraftUtils.lwjglJars = new String[]{"jinput.jar", "jutils.jar", "lwjgl.jar",
        "lwjgl_util.jar"};

    MinecraftUtils.setLwjglMacosxNativesForArchitecture();
    MinecraftUtils.setLwjglLinuxNativesForArchitecture();
    MinecraftUtils.setLwjglWindowsNativesForArchitecture();

    try {
      MinecraftUtils.lwjglUrl = new URL(
          "https://github.com/Kawaxte/TwentyTenLauncher/raw/nightly/libraries/lwjgl");
      MinecraftUtils.minecraftJarUrl = new URL(
          "https://github.com/Kawaxte/TwentyTenLauncher/raw/nightly/libraries/client");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private MinecraftUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getSubFolder(String version) {
    switch (version.charAt(0)) {
      case 'b':
        return "beta";
      case 'a':
        return "alpha";
      case 'i':
        return "infdev";
      default:
        break;
    }
    return null;
  }

  public static String getVersion(String version) {
    switch (version.charAt(0)) {
      case 'b':
        version = MessageFormat.format("Beta {0}", version.substring(1));
        break;
      case 'a':
        version = MessageFormat.format("Alpha v{0}", version.substring(1));
        break;
      case 'i':
        version = "Infdev";
        break;
      default:
        break;
    }
    return version;
  }

  private static void setLwjglMacosxNativesForArchitecture() {
    MinecraftUtils.lwjglMacosxNativesForAARCH64 = new String[]{"liblwjgl.dylib", "openal.dylib"};
    MinecraftUtils.lwjglMacosxNativesForAMD64 = new String[]{"libinput_osx.jnilib",
        "liblwjgl.dylib",
        "openal.dylib"};
  }

  private static void setLwjglLinuxNativesForArchitecture() {
    MinecraftUtils.LwjglLinuxNativesForAARCH64 = new String[]{"libjinput-linux64.so",
        "liblwjgl64.so", "libopenal64.so"};
    MinecraftUtils.lwjglLinuxNativesForAMD64 = new String[]{"libjinput-linux64.so", "liblwjgl64.so",
        "libopenal64.so"};
    MinecraftUtils.lwjglLinuxNativesForAARCH32 = new String[]{"libjinput-linux.so", "liblwjgl.so",
        "libopenal.so"};
    MinecraftUtils.lwjglLinuxNativesForX86 = new String[]{"libjinput-linux.so", "liblwjgl.so",
        "libopenal.so"};
  }

  private static void setLwjglWindowsNativesForArchitecture() {
    MinecraftUtils.lwjglWindowsNativesForAMD64 = new String[]{"jinput-dx8_64.dll",
        "jinput-raw_64.dll", "jinput-wintab.dll", "lwjgl64.dll", "OpenAL64.dll"};
    MinecraftUtils.lwjglWindowsNativesForX86 = new String[]{"jinput-dx8.dll", "jinput-raw.dll",
        "jingput-wintab.dll", "lwjgl.dll", "OpenAL32.dll"};
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
    for (String nativeFile : platform == EPlatform.MACOSX
        ? MinecraftUtils.lwjglMacosxNativesForAMD64
        : platform == EPlatform.LINUX ? MinecraftUtils.lwjglLinuxNativesForAMD64
            : MinecraftUtils.lwjglWindowsNativesForAMD64) {
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
            Objects.equals(SystemUtils.osArch, "aarch64")
                ? MinecraftUtils.lwjglMacosxNativesForAARCH64
                : MinecraftUtils.lwjglMacosxNativesForAMD64);
        break;
      case LINUX:
        switch (SystemUtils.osArch) {
          case "aarch64":
            MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
                MinecraftUtils.LwjglLinuxNativesForAARCH64);
            break;
          case "aarch32":
            MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
                MinecraftUtils.lwjglLinuxNativesForAARCH32);
            break;
          case "amd64":
            MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
                MinecraftUtils.lwjglLinuxNativesForAMD64);
            break;
          default:
            MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
                MinecraftUtils.lwjglLinuxNativesForX86);
            break;
        }
        break;
      case WINDOWS:
        MinecraftUtils.checkForLwjglNativeLibraryFiles(urls, url,
            Objects.equals(SystemUtils.osArch, "amd64")
                ? MinecraftUtils.lwjglWindowsNativesForAMD64
                : MinecraftUtils.lwjglWindowsNativesForX86);
        break;
      default:
        LoggerUtils.logMessage(String.valueOf(platform), ELevel.ERROR);
        break;
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
    LoggerUtils.logMessage(username, ELevel.INFO);
    MinecraftUtils.getInstance().launch(username, sessionId);
  }
}
