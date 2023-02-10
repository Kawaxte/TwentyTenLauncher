package net.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LanguageHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RequestHelper;
import java.applet.Applet;
import java.io.File;
import java.io.FileNotFoundException;
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

public class MinecraftUpdateImpl extends MinecraftUpdate implements Runnable {

  private static final String LWJGL_JAR_URL;
  private static final String LWJGL_NATIVE_URL;
  private static final String CLIENT_JAR_URL;
  private static final String[] LWJGL_JAR_NAMES;
  private static final String[] LWJGL_NATIVE_DYLIB_NAMES;
  private static final String[] LWJGL_NATIVE_SO_NAMES;
  private static final String[] LWJGL_NATIVE_DLL_NAMES;

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
  }

  private final String fieldNotFoundText;
  private final String classNotFoundText;
  private final String classInstantiationText;
  private final String illegalFieldAccessText;
  private final String illegalClassAccessText;
  private final String fileNotFoundText;
  private final String packageNotFoundText;
  private final String packageDownloadErrorText;
  private final String packageMoveErrorText;
  private final String fatalErrorMessageText;
  private final String subtaskDownloadText;
  @Getter
  protected String fatalErrorMessage;
  @Getter
  protected String subtaskMessage;
  @Getter
  protected int totalPercentage;
  private URL[] packageUrls;
  private ClassLoader loader;
  @Getter
  private boolean fatalErrorOccurred;

  {
    this.fieldNotFoundText = LanguageHelper.getString("mui.string.fieldNotFound.text");
    this.classNotFoundText = LanguageHelper.getString("mui.string.classNotFound.text");
    this.classInstantiationText = LanguageHelper.getString("mui.string.classInstantiation.text");
    this.illegalFieldAccessText = LanguageHelper.getString("mui.string.illegalFieldAccess.text");
    this.illegalClassAccessText = LanguageHelper.getString("mui.string.illegalClassAccess.text");
    this.fileNotFoundText = LanguageHelper.getString("mui.string.fileNotFound.text");
    this.packageNotFoundText = LanguageHelper.getString("mui.string.packageNotFound.text");
    this.packageDownloadErrorText = LanguageHelper.getString(
        "mui.string.packageDownloadError.text");
    this.packageMoveErrorText = LanguageHelper.getString("mui.string.packageMoveError.text");
    this.fatalErrorMessageText = LanguageHelper.getString("mui.string.fatalErrorMessage.text");
    this.subtaskDownloadText = LanguageHelper.getString("mui.string.subtaskDownload.text");
  }

  public MinecraftUpdateImpl() {
    this.subtaskMessage = "";
    this.fatalErrorMessage = "";

    this.fatalErrorOccurred = false;
  }

  public static boolean packageCached() {
    EState.setState(EState.CACHE_STATE);

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    File workingDirectory = FileHelper.workingDirectory;
    File binDirectory = new File(workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    File versionsDirectory = new File(workingDirectory, "versions");
    File clientJarDirectory = new File(versionsDirectory,
        LauncherConfig.instance.getSelectedVersion());
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

    String clientJarName = java.lang.String.format("%s.jar",
        LauncherConfig.instance.getSelectedVersion());
    return new File(clientJarDirectory, clientJarName).exists();
  }

  private int getTotalPackageSize(int[] sizes, int size) {
    EState.setState(EState.RETRIEVE_STATE);
    for (int urlIndex = 0; urlIndex < this.packageUrls.length; urlIndex++) {
      String url = this.packageUrls[urlIndex].toString();

      HttpsURLConnection connection = RequestHelper.performRequest(url, "GET",
          RequestHelper.xWwwFormHeader, false);
      Objects.requireNonNull(connection, "connection == null!");
      try {
        sizes[urlIndex] = connection.getContentLength();
        size += sizes[urlIndex];

        this.totalPercentage = 5 + (((urlIndex + 1) * 5) / this.packageUrls.length);
      } finally {
        connection.disconnect();
      }
    }
    return size;
  }

  private void updateClasspath() {
    EState.setState(EState.CLASSPATH_STATE);

    String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());

    File binDirectory = new File(FileHelper.workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
    File selectedVersionDirectory = new File(versionsDirectory,
        LauncherConfig.instance.getSelectedVersion());
    File clientJarFile = new File(selectedVersionDirectory, clientJarName);

    URL[] jarUrls = new URL[LWJGL_JAR_NAMES.length + 1];
    try {
      for (int i = 0; i < LWJGL_JAR_NAMES.length; i++) {
        jarUrls[i] = new File(binDirectory, LWJGL_JAR_NAMES[i]).toURI().toURL();
      }
      jarUrls[jarUrls.length - 1] = clientJarFile.toURI().toURL();

      this.totalPercentage = 85 + ((jarUrls.length * 5) / jarUrls.length);
    } catch (MalformedURLException murle) {
      this.showFatalError(murle.getMessage(), murle);
      return;
    }

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

    boolean loaded = this.nativesLoaded(nativesDirectory);
    if (!loaded) {
      LoggerHelper.logError("Failed to load natives", true);
      return;
    }

    LoggerHelper.logInfo(Arrays.toString(jarUrls), true);
  }

  private boolean nativesLoaded(File f) {
    boolean loaded = nativesLoaded(f, "loadedLibraryNames") || nativesLoaded(f, "nativeLibraries")
        || nativesLoaded(f, "systemNativeLibraries");
    if (loaded) {
      return true;
    }

    LoggerHelper.logError("Failed to load natives", true);
    return false;
  }

  private boolean nativesLoaded(File f, String name) {
    try {
      Field nativesField = ClassLoader.class.getDeclaredField(name);
      nativesField.setAccessible(true);

      Vector<?> loadedLibraryNames = (Vector<?>) nativesField.get(this.loader);
      loadedLibraryNames.clear();

      String[] libraryPaths = new String[]{"org.lwjgl.librarypath",
          "net.java.games.input.librarypath"};
      for (String libraryPath : libraryPaths) {
        System.setProperty(libraryPath, f.getAbsolutePath());
      }
      return true;
    } catch (NoSuchFieldException nsfe) {
      this.showFatalError(String.format(this.fieldNotFoundText, name), nsfe);
    } catch (IllegalAccessException iae) {
      this.showFatalError(String.format(this.illegalFieldAccessText, name), iae);
    }
    return false;
  }

  public Applet createAppletInstance() {
    String appletClassName = "MinecraftApplet";
    Class<?> minecraftAppletClass;
    try {
      minecraftAppletClass = this.loader.loadClass("net.minecraft.client.MinecraftApplet");
      return (Applet) minecraftAppletClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      this.showFatalError(String.format(this.classNotFoundText, appletClassName), cnfe);
    } catch (InstantiationException ie) {
      this.showFatalError(String.format(this.classInstantiationText, appletClassName), ie);
    } catch (IllegalAccessException iae) {
      this.showFatalError(String.format(this.illegalClassAccessText, appletClassName), iae);
    }
    return null;
  }

  private void showFatalError(String message, Throwable t) {
    this.fatalErrorOccurred = true;

    int state = EState.getState().ordinal();
    this.fatalErrorMessage = String.format(this.fatalErrorMessageText, state, message);
    this.subtaskMessage = "";

    LoggerHelper.logError(this.fatalErrorMessage, t, true);
  }

  @Override
  void loadPackage() {
    String lwjglJarUrl = null;
    String lwjglNativeUrl = null;
    String clientJarUrl = null;

    EState.setState(EState.DETERMINE_STATE);
    try {
      EPlatform platform = EPlatform.getPlatform();
      Objects.requireNonNull(platform, "platform == null!");

      String platformName = platform.name().toLowerCase(Locale.getDefault());
      String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());
      String[] lwjglNativeNames = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
          : platform == EPlatform.LINUX ? LWJGL_NATIVE_SO_NAMES : LWJGL_NATIVE_DLL_NAMES;

      File binDirectory = new File(FileHelper.workingDirectory, "bin");
      File nativesDirectory = new File(binDirectory, "natives");
      File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
      File selectedVersionDirectory = new File(versionsDirectory,
          LauncherConfig.instance.getSelectedVersion());
      File clientJarFile = new File(selectedVersionDirectory, clientJarName);

      String versionType = null;
      boolean isUsingBeta = LauncherConfig.instance.getUsingBeta();
      boolean isUsingAlpha = LauncherConfig.instance.getUsingAlpha();
      boolean isUsingInfdev = LauncherConfig.instance.getUsingInfdev();
      if (isUsingBeta) {
        versionType = OptionsHelper.versionTypes[0];
      }
      if (isUsingAlpha) {
        versionType = OptionsHelper.versionTypes[1];
      }
      if (isUsingInfdev) {
        versionType = OptionsHelper.versionTypes[2];
      }

      List<URL> urlList = new ArrayList<>();
      for (String lwjglJar : LWJGL_JAR_NAMES) {
        lwjglJarUrl = String.format(LWJGL_JAR_URL, lwjglJar);

        File lwjglJarFile = new File(binDirectory, lwjglJar);
        if (!lwjglJarFile.exists()) {
          urlList.add(new URL(lwjglJarUrl));
        }
      }
      for (String lwjgl : lwjglNativeNames) {
        lwjglNativeUrl = String.format(LWJGL_NATIVE_URL, platformName, lwjgl);

        File lwjglNativeFile = new File(nativesDirectory, lwjgl);
        if (!lwjglNativeFile.exists()) {
          urlList.add(new URL(lwjglNativeUrl));
        }
      }
      clientJarUrl = String.format(CLIENT_JAR_URL, versionType, clientJarName);
      if (!clientJarFile.exists()) {
        urlList.add(new URL(clientJarUrl));
      }

      this.packageUrls = urlList.toArray(new URL[0]);
      this.totalPercentage = 5;

      LoggerHelper.logInfo(Arrays.toString(this.packageUrls), true);
    } catch (MalformedURLException murle) {
      String fileName = murle.getMessage().substring(murle.getMessage().lastIndexOf("/") + 1);
      if (lwjglJarUrl == null || lwjglNativeUrl == null || clientJarUrl == null) {
        this.showFatalError(String.format(this.fileNotFoundText, fileName), murle);
        return;
      }
      this.showFatalError(this.packageNotFoundText, murle);
    }
  }

  @Override
  void downloadPackage() {
    int[] packageSizes = new int[this.packageUrls.length];
    int currentPackageSize = 0;
    int totalPackageSize = this.getTotalPackageSize(packageSizes, currentPackageSize);

    EState.setState(EState.DOWNLOAD_STATE);
    for (int urlIndex = 0; urlIndex < this.packageUrls.length; urlIndex++) {
      String packageUrl = this.packageUrls[urlIndex].toString();
      String packageName = packageUrl.substring(packageUrl.lastIndexOf('/') + 1);

      File tempDirectory = FileHelper.createDirectory(FileHelper.workingDirectory, ".temp");
      File packageFile = new File(tempDirectory, packageName);

      HttpsURLConnection connection = RequestHelper.performRequest(packageUrl, "GET",
          RequestHelper.xWwwFormHeader, true);
      Objects.requireNonNull(connection, "connection == null!");
      try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(
          packageFile)) {
        byte[] byteBuffer = new byte[65536];
        int bytesRead;
        int packageSize = 0;
        while ((bytesRead = is.read(byteBuffer)) != -1) {
          fos.write(byteBuffer, 0, bytesRead);
          packageSize += bytesRead;
          currentPackageSize += bytesRead;

          int packagePercentage = (int) (((packageSize * 1.0d) / packageSizes[urlIndex]) * 100.0d);
          this.subtaskMessage = String.format(this.subtaskDownloadText, packageName,
              packagePercentage);
          this.totalPercentage = 10 + ((currentPackageSize * 45) / totalPackageSize);
        }
      } catch (FileNotFoundException fnfe) {
        this.showFatalError(this.packageNotFoundText, fnfe);
        return;
      } catch (IOException ioe) {
        this.showFatalError(this.packageDownloadErrorText, ioe);
        return;
      } finally {
        connection.disconnect();
      }
    }
    this.subtaskMessage = "";
  }

  @Override
  void movePackage() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    File tempDirectory = new File(FileHelper.workingDirectory, ".temp");
    File[] tempPackages = tempDirectory.listFiles();
    Objects.requireNonNull(tempPackages, "tempPackages == null!");

    int totalPackageSize = tempPackages.length;
    int movedPackages = 0;

    EState.setState(EState.MOVE_STATE);
    for (File tempPackage : tempPackages) {
      File binDirectory = FileHelper.createDirectory(FileHelper.workingDirectory, "bin");
      Objects.requireNonNull(binDirectory, "binDirectory == null!");

      File nativesDirectory = FileHelper.createDirectory(binDirectory, "natives");
      Objects.requireNonNull(nativesDirectory, "nativesDirectory == null!");

      File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
      File selectedVersionDirectory = FileHelper.createDirectory(versionsDirectory,
          LauncherConfig.instance.getSelectedVersion());
      Objects.requireNonNull(selectedVersionDirectory, "selectedVersionDirectory == null!");

      String tempPackageName = tempPackage.getName();
      String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());
      String[] lwjglNativeNames = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
          : platform == EPlatform.WINDOWS ? LWJGL_NATIVE_DLL_NAMES
              : platform == EPlatform.LINUX ? LWJGL_NATIVE_SO_NAMES : null;

      try {
        for (String lwjglNative : lwjglNativeNames) {
          if (tempPackageName.equals(lwjglNative)) {
            Files.move(tempPackage.toPath(),
                nativesDirectory.toPath().resolve(tempPackage.toPath().getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
          }
        }
        for (String lwjglJarName : LWJGL_JAR_NAMES) {
          if (tempPackageName.equals(lwjglJarName)) {
            Files.move(tempPackage.toPath(),
                binDirectory.toPath().resolve(tempPackage.toPath().getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
          }
        }

        if (tempPackageName.equals(clientJarName)) {
          Files.move(tempPackage.toPath(),
              selectedVersionDirectory.toPath().resolve(tempPackage.toPath().getFileName()),
              StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException ioe) {
        this.showFatalError(this.packageMoveErrorText, ioe);
        return;
      }

      movedPackages++;
      this.totalPercentage = 55 + ((movedPackages * 30) / totalPackageSize);
    }

    FileHelper.deleteDirectory(tempDirectory);
  }

  @Override
  public void run() {
    EState.setState(EState.INIT_STATE);
    try {
      this.totalPercentage = 0;

      boolean cached = MinecraftUpdateImpl.packageCached();
      if (!cached) {
        this.loadPackage();
        this.downloadPackage();
      }
      this.movePackage();
      this.updateClasspath();

      EState.setState(EState.DONE_STATE);
      this.totalPercentage = 95;
    } catch (Throwable t) {
      this.showFatalError(t.getMessage(), t);
    }
  }
}
