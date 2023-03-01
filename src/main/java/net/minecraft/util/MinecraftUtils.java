package net.minecraft.util;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELogLevel;
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
import org.json.JSONArray;
import org.json.JSONObject;

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
      LoggerUtils.log("Failed to create URL", murle, ELogLevel.ERROR);
    }
  }

  private MinecraftUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getLwjglJarFile(List<URL> urls, URL url) {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File lwjglJarFile = new File(binDirectory, FileUtils.getFileNameFromUrl(url));
    if (!lwjglJarFile.exists()) {
      urls.add(url);
    }
  }

  public static void getLwjglNativeLibraries(List<URL> urls, URL url,
      String[] nativeLibraries) {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    for (String nativeLibrary : nativeLibraries) {
      File lwjglNativeLibraryFile = new File(nativesDirectory, nativeLibrary);
      if (!lwjglNativeLibraryFile.exists()) {
        urls.add(url);
        break;
      }
    }
  }

  public static void getMinecraftJarFile(List<URL> urls, URL url) {
    File versionDirectory = new File(VersionUtils.versionsDirectory,
        ConfigUtils.getInstance().getSelectedVersion());
    File minecraftJarFile = new File(versionDirectory,
        MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion()));
    if (!minecraftJarFile.exists()) {
      urls.add(url);
    }
  }

  public static int getProxyPort(String version) {
    File versionsFile = new File(VersionUtils.versionsDirectory, "versions.json");
    JSONObject versionJson = FileUtils.readJsonFile(versionsFile);

    String versionType = MinecraftUtils.getVersionType(version);
    for (String versionTypeKey : versionJson.keySet()) {
      if (versionTypeKey.equals(versionType)) {
        JSONArray versionArray = versionJson.getJSONArray(versionTypeKey);
        return MinecraftUtils.getProxyPortFromVersionArray(versionArray, version);
      }
    }
    return Integer.parseInt("80");
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

  public static void launchMinecraft() {
    MinecraftUtils.getInstance().launch();
  }

  public static void launchMinecraft(String username, String sessionId) {
    MinecraftUtils.getInstance().launch(username, sessionId);
  }

  private static String getVersionType(String version) {
    switch (version.charAt(0)) {
      case 'b':
        return "beta";
      case 'a':
        return "alpha";
      case 'i':
        return "infdev";
      default:
        return null;
    }
  }

  private static int getProxyPortFromVersionArray(JSONArray array, String version) {
    for (int i = 0; i < array.length(); i++) {
      JSONObject object = array.getJSONObject(i);
      if (object.getString("version_id").equals(version)) {
        return object.getInt("proxy_port");
      }
    }
    return Integer.parseInt("80");
  }
}
