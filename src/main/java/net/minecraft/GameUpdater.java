package net.minecraft;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.FileManager;
import ee.twentyten.util.LoggingManager;
import ee.twentyten.util.OptionsManager;
import ee.twentyten.util.RequestManager;
import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;

public class GameUpdater implements Runnable {

  private static final String LWJGL_JAR_URL;
  private static final String LWJGL_NATIVE_URL;
  private static final String CLIENT_JAR_URL;
  private static final String[] LWJGL_JAR_NAMES;
  private static final String[] LWJGL_NATIVE_DYLIB_NAMES;
  private static final String[] LWJGL_NATIVE_SO_NAMES;
  private static final String[] LWJGL_NATIVE_DLL_NAMES;
  private static final String CLIENT_JAR_NAME;

  static {
    LWJGL_JAR_URL = "https://archive.org/download/lwjgl-2/lwjgl-2.9.3/jar/%s";
    LWJGL_NATIVE_URL = "https://archive.org/download/lwjgl-2/lwjgl-2.9.3/native/%s/%s";
    CLIENT_JAR_URL = "https://archive.org/download/legacy-minecraft-%s/%s";

    LWJGL_JAR_NAMES = new String[]{"jinput.jar", "lwjgl.jar", "lwjgl_util.jar"};
    LWJGL_NATIVE_SO_NAMES = new String[]{"libjinput-linux.so", "libjinput-linux64.so",
        "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so"};
    LWJGL_NATIVE_DYLIB_NAMES = new String[]{"libjinput-osx.dylib", "liblwjgl.dylib",
        "openal.dylib"};
    LWJGL_NATIVE_DLL_NAMES = new String[]{"OpenAL32.dll", "OpenAL64.dll", "jinput-dx8.dll",
        "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll", "lwjgl.dll", "lwjgl64.dll"};
    CLIENT_JAR_NAME = Config.instance.getSelectedVersion();
  }

  @Getter
  protected String subtaskMessage;

  @Getter
  protected String errorMessage;

  protected ClassLoader loader;
  protected int state;

  @Getter
  protected int totalPercentage;

  @Getter
  private boolean fatalErrorOccurred;

  private boolean nativesLoaded;
  private URL[] urls;
  private int initialPercentage;

  public GameUpdater() {
    this.subtaskMessage = "";
    this.nativesLoaded = false;
    this.state = 1;
    this.totalPercentage = 0;
  }

  static boolean packageCached() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    File workingDirectory = FileManager.workingDirectory;
    File binDirectory = new File(workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    File versionsDirectory = new File(workingDirectory, "versions");
    File clientJarDirectory = new File(versionsDirectory, CLIENT_JAR_NAME);
    String[] nativeNames = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
        : platform == EPlatform.WINDOWS ? LWJGL_NATIVE_DLL_NAMES : LWJGL_NATIVE_SO_NAMES;
    for (String jarName : LWJGL_JAR_NAMES) {
      if (!new File(binDirectory, jarName).exists()) {
        return false;
      }
    }
    for (String nativeName : nativeNames) {
      if (!new File(nativesDirectory, nativeName).exists()) {
        return false;
      }
    }

    String clientJarName = String.format("%s.jar", CLIENT_JAR_NAME);
    return new File(clientJarDirectory, clientJarName).exists();
  }

  String getStateDescription() {
    switch (this.state) {
      case 1:
        return "Initialising loader";
      case 2:
        return "Checking cache for existing files";
      case 3:
        return "Determining packages to load";
      case 4:
        return "Downloading packages";
      case 5:
        return "Moving downloaded packages";
      case 6:
        return "Updating classpath";
      case 7:
        return "Done loading";
      default:
        return "Unknown state";
    }
  }

  private void determinePackages() {
    this.subtaskMessage = "";
    this.state = 3;
    this.totalPercentage = 5;

    boolean isUsingBeta = Config.instance.getUsingBeta();
    boolean isUsingAlpha = Config.instance.getUsingAlpha();
    boolean isUsingInfdev = Config.instance.getUsingInfdev();
    String versionType = null;
    if (isUsingBeta) {
      versionType = OptionsManager.versionTypes[0];
    }
    if (isUsingAlpha) {
      versionType = OptionsManager.versionTypes[1];
    }
    if (isUsingInfdev) {
      versionType = OptionsManager.versionTypes[2];
    }

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    String lwjglJarUrl = null;
    String lwjglNativeUrl = null;
    String clientJarUrl = null;
    String platformName = platform.name().toLowerCase(Locale.getDefault());
    String versionId = String.format("%s.jar", Config.instance.getSelectedVersion());
    try {
      List<URL> urlList = new ArrayList<>();

      String[] lwjglNative = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
          : platform == EPlatform.LINUX ? LWJGL_NATIVE_SO_NAMES : LWJGL_NATIVE_DLL_NAMES;
      for (String lwjglJar : LWJGL_JAR_NAMES) {
        lwjglJarUrl = String.format(LWJGL_JAR_URL, lwjglJar);
        urlList.add(new URL(lwjglJarUrl));
      }
      for (String lwjgl : lwjglNative) {
        lwjglNativeUrl = String.format(LWJGL_NATIVE_URL, platformName, lwjgl);
        urlList.add(new URL(lwjglNativeUrl));
      }
      clientJarUrl = String.format(CLIENT_JAR_URL, versionType, versionId);
      urlList.add(new URL(clientJarUrl));

      this.urls = urlList.toArray(new URL[0]);
      LoggingManager.logInfo(this.getClass(), Arrays.toString(this.urls));
    } catch (MalformedURLException mue) {
      if (lwjglJarUrl == null) {
        this.showError("Failed to find lwjgl jar files");
        LoggingManager.logError(this.getClass(), "Failed to find lwjgl jar files", mue);
      }
      if (lwjglNativeUrl == null) {
        this.showError("Failed to find lwjgl native files");
        LoggingManager.logError(this.getClass(), "Failed to find lwjgl native files", mue);
      }
      if (clientJarUrl == null) {
        this.showError("Failed to find client jar file");
        LoggingManager.logError(this.getClass(), "Failed to find client jar file", mue);
      }
      LoggingManager.logError(this.getClass(), "Failed to determine packages", mue);
    }
  }

  private void downloadPackages() throws IOException {
    this.state = 4;

    int currentPackageSize = 0;
    int totalPackageSize = 0;
    int[] packageSizes = new int[this.urls.length];
    String packageUrl;
    String packageName;

    HttpsURLConnection connection;
    for (int urlIndex = 0; urlIndex < this.urls.length; urlIndex++) {
      packageUrl = this.urls[urlIndex].toString();
      connection = RequestManager.sendHttpRequest(packageUrl, "HEAD",
          RequestManager.NO_CACHE_HEADER);
      Objects.requireNonNull(connection, "connection == null!");

      packageSizes[urlIndex] = connection.getContentLength();
      totalPackageSize += packageSizes[urlIndex];

      this.initialPercentage = 10;
      this.initialPercentage += ((urlIndex + 1) * 5) / this.urls.length;

      packageName = packageUrl.substring(packageUrl.lastIndexOf('/') + 1);
      this.subtaskMessage = String.format("Retrieving: %s", packageName);
      this.totalPercentage = initialPercentage;
    }
    for (int urlIndex = 0; urlIndex < this.urls.length; urlIndex++) {
      packageUrl = this.urls[urlIndex].toString();
      packageName = packageUrl.substring(packageUrl.lastIndexOf('/') + 1);

      File tempDirectory = new File(FileManager.workingDirectory, ".temp");
      if (!tempDirectory.exists()) {
        boolean created = tempDirectory.mkdir();
        if (!created) {
          this.showError("Failed to create temp directory");
          LoggingManager.logError(this.getClass(), "Failed to create temp directory");
        }
      }

      File packageFile = new File(tempDirectory, packageName);
      connection = RequestManager.sendHttpRequest(packageUrl, "GET",
          RequestManager.X_WWW_FORM_HEADER);
      Objects.requireNonNull(connection, "connection == null!");
      try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(
          packageFile)) {
        long downloadStartTime = System.currentTimeMillis();

        byte[] byteBuffer = new byte[65536];
        int bytesRead;
        int packageSize = 0;
        while ((bytesRead = is.read(byteBuffer)) != -1) {
          fos.write(byteBuffer, 0, bytesRead);
          packageSize += bytesRead;

          int percentagePerPackage = (int) (((packageSize * 1D) / packageSizes[urlIndex]) * 100D);
          this.subtaskMessage = String.format("Downloading: %s %d%%", packageName,
              percentagePerPackage);

          currentPackageSize += bytesRead;
          this.initialPercentage = 15;
          this.initialPercentage += (currentPackageSize * 45) / totalPackageSize;
          this.totalPercentage = this.initialPercentage;

          long downloadTime = System.currentTimeMillis() - downloadStartTime;
          if (downloadTime >= 1000L) {
            double downloadSpeed = packageSize / (downloadTime / 1.0E03);

            String downloadSpeedMessage =
                downloadSpeed >= 1.024E03 ? String.format("%.2f KB/s", downloadSpeed / 1.024E03)
                    : String.format("%.2f B/s", downloadSpeed);
            this.subtaskMessage = String.format("Downloading: %s %d%% @ %s", packageName,
                percentagePerPackage, downloadSpeedMessage);
          }
        }
      }
    }
    this.subtaskMessage = "";
  }

  private void movePackages() throws IOException {
    this.state = 5;

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    File binDirectory = this.createDirectory(FileManager.workingDirectory, "bin");
    File nativesDirectory = this.createDirectory(binDirectory, "natives");
    File versionsDirectory = this.createDirectory(FileManager.workingDirectory, "versions");
    File selectedVersionDirectory = this.createDirectory(versionsDirectory, CLIENT_JAR_NAME);

    File tempDirectory = this.createDirectory(FileManager.workingDirectory, ".temp");
    File[] tempPackages = tempDirectory.listFiles();
    Objects.requireNonNull(tempPackages, "tempPackages == null!");
    int totalPackageSize = tempPackages.length;
    int movedPackages = 0;
    for (File tempPackage : tempPackages) {
      String tempPackageName = tempPackage.getName();
      String[] lwjglNativeName = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
          : platform == EPlatform.WINDOWS ? LWJGL_NATIVE_DLL_NAMES
              : platform == EPlatform.LINUX ? LWJGL_NATIVE_SO_NAMES : null;
      String clientJarName = String.format("%s.jar", CLIENT_JAR_NAME);
      for (String lwjglJarName : LWJGL_JAR_NAMES) {
        if (tempPackageName.equals(lwjglJarName)) {
          Files.move(tempPackage.toPath(), new File(binDirectory, tempPackageName).toPath(),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
      for (String lwjglNative : lwjglNativeName) {
        if (tempPackageName.equals(lwjglNative)) {
          Files.move(tempPackage.toPath(), new File(nativesDirectory, tempPackageName).toPath(),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
      if (tempPackageName.equals(clientJarName)) {
        Files.move(tempPackage.toPath(),
            new File(selectedVersionDirectory, tempPackageName).toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      }

      this.subtaskMessage = String.format("Moving: %s", tempPackageName);
      movedPackages++;

      this.initialPercentage = 60;
      this.initialPercentage += (movedPackages * 15) / totalPackageSize;
      this.totalPercentage = this.initialPercentage;
    }
    if (tempDirectory.exists()) {
      boolean deleted = tempDirectory.delete();
      if (!deleted) {
        this.showError("Failed to delete temp directory");
        LoggingManager.logError(this.getClass(), "Failed to delete temp directory");
      }
    }
    this.subtaskMessage = "";
  }

  private void updateClasspath() {
    this.state = 6;

    File binDirectory = new File(FileManager.workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    File versionsDirectory = new File(FileManager.workingDirectory, "versions");
    File selectedVersionDirectory = new File(versionsDirectory, CLIENT_JAR_NAME);

    String clientJarName = String.format("%s.jar", CLIENT_JAR_NAME);
    File clientJarFile = new File(selectedVersionDirectory, clientJarName);
    URL[] jarUrls = new URL[LWJGL_JAR_NAMES.length + 1];
    try {
      for (int i = 0; i < LWJGL_JAR_NAMES.length; i++) {
        jarUrls[i] = new File(binDirectory, LWJGL_JAR_NAMES[i]).toURI().toURL();
      }
      jarUrls[jarUrls.length - 1] = clientJarFile.toURI().toURL();

      this.initialPercentage = 75 + (jarUrls.length * 15) / jarUrls.length;
      this.totalPercentage = this.initialPercentage;
    } catch (MalformedURLException mue) {
      this.showError("Failed to construct URL from URI");
      LoggingManager.logError(this.getClass(), "Failed to construct URL from URI", mue);
    }
    LoggingManager.logInfo(this.getClass(), Arrays.toString(jarUrls));

    if (this.loader == null) {
      final URL[] finalJarUrls = jarUrls;
      this.loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
        @Override
        public ClassLoader run() {
          URLClassLoader classLoader = new URLClassLoader(finalJarUrls,
              Thread.currentThread().getContextClassLoader());
          Thread.currentThread().setContextClassLoader(classLoader);
          return classLoader;
        }
      });
    }

    if (!this.nativesLoaded) {
      try {
        Field nativesField = ClassLoader.class.getDeclaredField("loadedLibraryNames");
        nativesField.setAccessible(true);
        Vector<?> loadedLibraryNames = (Vector<?>) nativesField.get(this.loader);
        loadedLibraryNames.clear();
      } catch (NoSuchFieldException nsfe) {
        this.showError("Failed to find declared field");
        LoggingManager.logError(getClass(), "Failed to find declared field", nsfe);
      } catch (IllegalAccessException iae) {
        this.showError("Failed to access declared field");
        LoggingManager.logError(getClass(), "Failed to access declared field", iae);
      }
    }

    String[] libraryPaths = new String[]{"org.lwjgl.librarypath",
        "net.java.games.input.librarypath"};
    for (String libraryPath : libraryPaths) {
      System.setProperty(libraryPath, nativesDirectory.getAbsolutePath());
    }
    this.nativesLoaded = true;
  }

  private File createDirectory(File parent, String name) {
    File directory = new File(parent, name);
    if (!directory.exists()) {
      boolean created = directory.mkdir();
      if (!created) {
        this.showError("Failed to create directory");
        LoggingManager.logError(this.getClass(), "Failed to create directory");
      }
    }
    return directory;
  }

  protected Applet createAppletInstance() {
    Class<?> minecraftAppletClass;
    try {
      minecraftAppletClass = this.loader.loadClass("net.minecraft.client.MinecraftApplet");
      return (Applet) minecraftAppletClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      this.showError("Can't find MinecraftApplet class");
      LoggingManager.logError(this.getClass(), "Failed to load MinecraftApplet class", cnfe);
    } catch (InstantiationException ie) {
      this.showError("Can't instantiate MinecraftApplet class");
      LoggingManager.logError(this.getClass(), "Can't instantiate MinecraftApplet class", ie);
    } catch (IllegalAccessException iae) {
      this.showError("Failed to access MinecraftApplet class");
      LoggingManager.logError(this.getClass(), "Failed to access MinecraftApplet class", iae);
    }
    return null;
  }

  private void showError(String message) {
    this.fatalErrorOccurred = true;

    this.errorMessage = String.format("Fatal error occurred (%s): %s", this.state, message);
    this.subtaskMessage = "";
  }

  @Override
  public void run() {
    this.state = 2;
    this.totalPercentage = 5;

    try {
      boolean cached = GameUpdater.packageCached();
      if (!cached) {
        this.determinePackages();
        this.downloadPackages();
        this.movePackages();
      }
      this.updateClasspath();

      this.state = 7;
      this.totalPercentage = 95;
    } catch (Throwable t) {
      this.showError("Failed to run game updater");
      LoggingManager.logError(this.getClass(), "Failed to run game updater", t);
    }
  }
}
