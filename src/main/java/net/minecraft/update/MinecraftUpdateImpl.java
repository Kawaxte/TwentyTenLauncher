package net.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LogHelper;
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
  private static final Class<MinecraftUpdateImpl> CLASS_REF;

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

    CLASS_REF = MinecraftUpdateImpl.class;
  }

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

  public MinecraftUpdateImpl() {
    this.subtaskMessage = "";
    this.fatalErrorMessage = "";

    this.fatalErrorOccurred = false;
  }

  public static boolean packageCached() {
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

    String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());
    return new File(clientJarDirectory, clientJarName).exists();
  }

  private int getTotalPackageSize(int size, int[] sizes) {
    EState.setState(EState.RETRIEVE_STATE);
    for (int urlIndex = 0; urlIndex < this.packageUrls.length; urlIndex++) {
      String url = this.packageUrls[urlIndex].toString();
      String name = url.substring(url.lastIndexOf('/') + 1);

      HttpsURLConnection connection = RequestHelper.performRequest(url, "HEAD",
          RequestHelper.xWwwFormHeader, false);
      Objects.requireNonNull(connection, "connection == null");
      sizes[urlIndex] = connection.getContentLength();
      size += sizes[urlIndex];

      this.subtaskMessage = String.format("Retrieving: %s", name);
      this.totalPercentage = 5 + (((urlIndex + 1) * 5) / this.packageUrls.length);
    }
    this.subtaskMessage = "";
    return size;
  }

  private void updateClasspath() {
    String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());

    File binDirectory = new File(FileHelper.workingDirectory, "bin");
    File nativesDirectory = new File(binDirectory, "natives");
    File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
    File selectedVersionDirectory = new File(versionsDirectory,
        LauncherConfig.instance.getSelectedVersion());
    File clientJarFile = new File(selectedVersionDirectory, clientJarName);

    URL[] jarUrls = new URL[LWJGL_JAR_NAMES.length + 1];
    
    EState.setState(EState.CLASSPATH_STATE);
    try {
      for (int i = 0; i < LWJGL_JAR_NAMES.length; i++) {
        jarUrls[i] = new File(binDirectory, LWJGL_JAR_NAMES[i]).toURI().toURL();
      }
      jarUrls[jarUrls.length - 1] = clientJarFile.toURI().toURL();

      this.totalPercentage = 85 + ((jarUrls.length * 5) / jarUrls.length);
    } catch (MalformedURLException murle) {
      this.showFatalError("Can't construct URL from URI", murle);
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
      LogHelper.logError(CLASS_REF, "Failed to load natives");
    }

    LogHelper.logInfo(CLASS_REF, Arrays.toString(jarUrls));
  }

  private boolean nativesLoaded(File nativesDirectory) {
    try {
      Field nativesField = ClassLoader.class.getDeclaredField("loadedLibraryNames");
      nativesField.setAccessible(true);

      Vector<?> loadedLibraryNames = (Vector<?>) nativesField.get(this.loader);
      loadedLibraryNames.clear();
    } catch (NoSuchFieldException nsfe) {
      this.showFatalError("Can't find loadedLibraryNames field", nsfe);
      return false;
    } catch (IllegalAccessException iae) {
      this.showFatalError("Can't access loadedLibraryNames field", iae);
      return false;
    }

    String[] libraryPaths = new String[]{"org.lwjgl.librarypath",
        "net.java.games.input.librarypath"};
    for (String libraryPath : libraryPaths) {
      System.setProperty(libraryPath, nativesDirectory.getAbsolutePath());
    }
    return true;
  }

  public Applet createAppletInstance() {
    Class<?> minecraftAppletClass;
    try {
      minecraftAppletClass = this.loader.loadClass("net.minecraft.client.MinecraftApplet");
      return (Applet) minecraftAppletClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      this.showFatalError("Can't find MinecraftApplet class", cnfe);
    } catch (InstantiationException ie) {
      this.showFatalError("Can't instantiate MinecraftApplet class", ie);
    } catch (IllegalAccessException iae) {
      this.showFatalError("Can't access MinecraftApplet class", iae);
    }
    return null;
  }

  private void showFatalError(String error, Throwable t) {
    this.fatalErrorOccurred = true;

    int state = EState.getState().ordinal();
    this.fatalErrorMessage = String.format("Fatal error occurred (%s): %s", state, error);

    LogHelper.logError(CLASS_REF, this.fatalErrorMessage, t);
  }

  @Override
  void determinePackage() {
    EState.setState(EState.DETERMINE_STATE);
    this.totalPercentage = 5;

    boolean isUsingBeta = LauncherConfig.instance.getUsingBeta();
    boolean isUsingAlpha = LauncherConfig.instance.getUsingAlpha();
    boolean isUsingInfdev = LauncherConfig.instance.getUsingInfdev();
    String versionType = null;
    if (isUsingBeta) {
      versionType = OptionsHelper.versionTypes[0];
    }
    if (isUsingAlpha) {
      versionType = OptionsHelper.versionTypes[1];
    }
    if (isUsingInfdev) {
      versionType = OptionsHelper.versionTypes[2];
    }

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    String lwjglJarUrl = null;
    String lwjglNativeUrl = null;
    String clientJarUrl = null;
    String platformName = platform.name().toLowerCase(Locale.getDefault());
    String versionId = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());
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

      this.packageUrls = urlList.toArray(new URL[0]);

      LogHelper.logInfo(CLASS_REF, Arrays.toString(this.packageUrls));
    } catch (MalformedURLException mue) {
      if (lwjglJarUrl == null) {
        this.showFatalError("Can't find lwjgl jar files", mue);
      }
      if (lwjglNativeUrl == null) {
        this.showFatalError("Can't find lwjgl native files", mue);
      }
      if (clientJarUrl == null) {
        this.showFatalError("Can't find client jar file", mue);
      }
      this.showFatalError("Can't find package files", mue);
    }
  }

  @Override
  void downloadPackage() {
    int[] packageSizes = new int[this.packageUrls.length];
    int currentPackageSize = 0;
    int totalPackageSize = this.getTotalPackageSize(currentPackageSize, packageSizes);

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

          int packagePercentage = (int) (((packageSize * 1.0d) / packageSizes[urlIndex]) * 100.d);
          this.subtaskMessage = String.format("Downloading: %s %d%%", packageName,
              packagePercentage);
          this.totalPercentage = 10 + ((currentPackageSize * 45) / totalPackageSize);
        }
      } catch (FileNotFoundException fnfe) {
        this.showFatalError("Can't find package file", fnfe);
      } catch (IOException ioe) {
        this.showFatalError("Can't download package file", ioe);
      }
    }
    this.subtaskMessage = "";
  }

  @Override
  void movePackage() throws IOException {
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
      File nativesDirectory = FileHelper.createDirectory(binDirectory, "natives");
      File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
      File selectedVersionDirectory = FileHelper.createDirectory(versionsDirectory,
          LauncherConfig.instance.getSelectedVersion());

      String tempPackageName = tempPackage.getName();
      String clientJarName = String.format("%s.jar", LauncherConfig.instance.getSelectedVersion());
      String[] lwjglNativeNames = platform == EPlatform.MACOSX ? LWJGL_NATIVE_DYLIB_NAMES
          : platform == EPlatform.WINDOWS ? LWJGL_NATIVE_DLL_NAMES
              : platform == EPlatform.LINUX ? LWJGL_NATIVE_SO_NAMES : null;
      if (tempPackageName.equals(clientJarName)) {
        Files.move(tempPackage.toPath(),
            new File(selectedVersionDirectory, tempPackageName).toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      }

      for (String lwjglNative : lwjglNativeNames) {
        if (tempPackageName.equals(lwjglNative)) {
          Files.move(tempPackage.toPath(), new File(nativesDirectory, tempPackageName).toPath(),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
      for (String lwjglJarName : LWJGL_JAR_NAMES) {
        if (tempPackageName.equals(lwjglJarName)) {
          Files.move(tempPackage.toPath(), new File(binDirectory, tempPackageName).toPath(),
              StandardCopyOption.REPLACE_EXISTING);
        }
      }

      this.subtaskMessage = String.format("Moving: %s", tempPackageName);

      movedPackages++;
      this.totalPercentage = 55 + ((movedPackages * 30) / totalPackageSize);
    }
    this.subtaskMessage = "";

    FileHelper.deleteDirectory(tempDirectory);
  }

  @Override
  public void run() {
    EState.setState(EState.INIT_STATE);
    this.totalPercentage = 0;

    try {
      boolean cached = MinecraftUpdateImpl.packageCached();
      if (!cached) {
        this.determinePackage();
        this.downloadPackage();
        this.movePackage();
      }
      this.updateClasspath();
    } catch (Throwable t) {
      this.showFatalError(t.getMessage(), t);
    }
  }
}
