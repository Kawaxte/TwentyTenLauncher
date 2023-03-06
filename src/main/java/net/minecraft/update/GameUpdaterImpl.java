package net.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.VersionUtils;
import java.applet.Applet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.util.MinecraftUtils;

public class GameUpdaterImpl extends GameUpdater implements Runnable {

  public Applet loadMinecraftApplet() {
    try {
      return (Applet) this.minecraftAppletLoader.loadClass("net.minecraft.client.MinecraftApplet")
          .newInstance();
    } catch (InstantiationException ie) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.instantation.class"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to instantiate MinecraftApplet class", ie, ELevel.ERROR);
    } catch (IllegalAccessException iae) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.illegalAccess.class"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to access MinecraftApplet class", iae, ELevel.ERROR);
    } catch (ClassNotFoundException cnfe) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.classNotFound"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to find MinecraftApplet class", cnfe, ELevel.ERROR);
    }
    return null;
  }

  private void unloadLibraries(File directory) {
    if (this.isLibrariesLoaded) {
      return;
    }

    try {
      if (SystemUtils.javaVersion.startsWith("1.7")) {
        this.unloadLibrariesOnJavaSeven(directory);
      }
      if (SystemUtils.javaVersion.startsWith("1.8")) {
        this.unloadLibrariesOnJavaEight(directory);
      }
      if (SystemUtils.javaVersion.startsWith("11")) {
        this.unloadLibrariesOnJavaEleven(directory);
      }
    } catch (NoSuchFieldException nsfe) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.fieldNotFound"),
          "loadedLibraryNames"));
      LoggerUtils.logMessage("Failed to find loadedLibraryNames field", nsfe, ELevel.ERROR);
    } catch (IllegalAccessException iae) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.illegalAccess.field"),
          "loadedLibraryNames"));
      LoggerUtils.logMessage("Failed to access loadedLibraryNames field", iae, ELevel.ERROR);
    }
  }

  private void loadLibraries(File directory) {
    String[] libraryPaths = new String[]{"org.lwjgl.librarypath",
        "net.java.games.input.librarypath"};
    for (String libraryPath : libraryPaths) {
      System.setProperty(libraryPath, directory.getAbsolutePath());
      LoggerUtils.logMessage(
          MessageFormat.format("{0}={1}", libraryPath, System.getProperty(libraryPath)),
          ELevel.INFO);
    }
    this.isLibrariesLoaded = true;
  }

  @Override
  void determinePackage() {
    EState.setInstance(EState.DETERMINE_PACKAGE);
    this.stateMessage = EState.DETERMINE_PACKAGE.getMessage();
    this.percentage = 5;

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");
    try {
      List<URL> packageUrls = new ArrayList<>();

      URL lwjglJarUrl;
      for (String lwjglJar : MinecraftUtils.lwjglJars) {
        lwjglJarUrl = new URL(MessageFormat.format("{0}/{1}", MinecraftUtils.lwjglUrl, lwjglJar));
        if (this.isContentLengthAvailable(lwjglJarUrl, FileUtils.getFileName(lwjglJarUrl))) {
          return;
        }
        MinecraftUtils.checkForLwjglJarFiles(packageUrls, lwjglJarUrl);
      }

      URL lwjglNativesUrl;
      switch (platform) {
        case MACOSX:
          lwjglNativesUrl = new URL(
              MessageFormat.format("{0}/natives-macosx.zip", MinecraftUtils.lwjglUrl));
          break;
        case LINUX:
          lwjglNativesUrl = new URL(
              MessageFormat.format("{0}/natives-linux.zip", MinecraftUtils.lwjglUrl));
          break;
        case WINDOWS:
          lwjglNativesUrl = new URL(
              MessageFormat.format("{0}/natives-windows.zip", MinecraftUtils.lwjglUrl));
          break;
        default:
          throw new IllegalStateException(String.valueOf(platform));
      }
      if (this.isContentLengthAvailable(lwjglNativesUrl, FileUtils.getFileName(lwjglNativesUrl))) {
        return;
      }
      MinecraftUtils.checkForLwjglNativeLibraryFiles(platform, packageUrls, lwjglNativesUrl);

      URL minecraftJarUrl = new URL(MessageFormat.format("{0}/{1}", MinecraftUtils.minecraftJarUrl,
          MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion())));
      if (this.isContentLengthAvailable(minecraftJarUrl, FileUtils.getFileName(minecraftJarUrl))) {
        return;
      }
      MinecraftUtils.checkForMinecraftJarFile(packageUrls, minecraftJarUrl);

      this.urls = packageUrls.toArray(new URL[0]);
      LoggerUtils.logMessage(Arrays.toString(this.urls), ELevel.INFO);
    } catch (MalformedURLException murle) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "gui.exception.io.package.determineFailed"));
      LoggerUtils.logMessage("Failed to determine package URLs", murle, ELevel.ERROR);
    }
  }

  @Override
  void downloadPackage() {
    EState.setInstance(EState.DOWNLOAD_PACKAGE);
    this.stateMessage = EState.DOWNLOAD_PACKAGE.getMessage();
    this.percentage = 10;

    int[] fileSizes = new int[this.urls.length];
    int currentDownloadSize = 0;
    int totalDownloadSize = this.getTotalDownloadSize(fileSizes, currentDownloadSize);
    for (URL fileUrl : this.urls) {
      File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
      if (!binDirectory.mkdirs() && !binDirectory.exists()) {
        LoggerUtils.logMessage("Failed to create bin directory", ELevel.ERROR);
        return;
      }

      File packageFile = new File(binDirectory, FileUtils.getFileName(fileUrl));
      if (Objects.equals(packageFile.getName(),
          MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion()))) {
        File versionDirectory = new File(VersionUtils.versionsDirectory,
            ConfigUtils.getInstance().getSelectedVersion());
        if (!versionDirectory.mkdirs() && !versionDirectory.exists()) {
          LoggerUtils.logMessage("Failed to create version directory", ELevel.ERROR);
          return;
        }
        packageFile = new File(versionDirectory, packageFile.getName());
      }

      HttpsURLConnection connection = RequestUtils.performHttpsRequest(fileUrl, EMethod.GET,
          EHeader.NO_CACHE.getHeader());
      Objects.requireNonNull(connection, "connection == null!");
      try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(
          packageFile)) {
        int bufferSize;
        int downloadedSize = 0;
        byte[] buffer = new byte[65536];
        long downloadStartTime = System.currentTimeMillis();
        String downloadSpeedMessage = "";
        while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
          fos.write(buffer, 0, bufferSize);

          currentDownloadSize += bufferSize;
          this.taskMessage = MessageFormat.format(
              LanguageUtils.getString(LanguageUtils.getBundle(), "gui.string.downloadTask"),
              FileUtils.getFileName(fileUrl), (currentDownloadSize * 100) / totalDownloadSize);
          this.percentage = 10 + ((currentDownloadSize * 45) / totalDownloadSize);

          downloadedSize += bufferSize;
          long downloadElapsedTime = System.currentTimeMillis() - downloadStartTime;
          if (downloadElapsedTime >= 1000L) {
            double downloadSpeed = downloadedSize / (double) downloadElapsedTime;
            downloadSpeed = (downloadSpeed * 100.0d) / 100.0d;
            downloadSpeedMessage = MessageFormat.format(" @ {0,number,#.##} KB/sec", downloadSpeed);

            downloadedSize = 0;
            downloadStartTime += 1000L;
          }
          this.taskMessage = this.taskMessage.concat(downloadSpeedMessage);
        }
      } catch (FileNotFoundException fnfe) {
        this.setFatalErrorMessage(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.fileNotFound"),
            packageFile.getName()));
        LoggerUtils.logMessage("Failed to find package file", fnfe, ELevel.ERROR);
      } catch (IOException ioe) {
        this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "gui.exception.io.package.downloadFailed"));
        LoggerUtils.logMessage("Failed to download package files", ioe, ELevel.ERROR);
      }
    }
    this.taskMessage = "";
  }

  @Override
  void extractPackage() {
    EState.setInstance(EState.EXTRACT_PACKAGE);
    this.stateMessage = EState.EXTRACT_PACKAGE.getMessage();
    this.percentage = 60;

    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File[] archiveFiles = binDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".zip");
      }
    });
    Objects.requireNonNull(archiveFiles, "packageFiles == null!");

    int currentExtractSize = 0;
    int totalExtractSuze = 0;
    for (File archiveFile : archiveFiles) {
      try (ZipFile zipFile = new ZipFile(archiveFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (entry.isDirectory()) {
            continue;
          }
          totalExtractSuze += entry.getSize();

          File nativesDirectory = new File(binDirectory, "natives");
          if (!nativesDirectory.mkdirs() && !nativesDirectory.exists()) {
            LoggerUtils.logMessage("Failed to create natives directory", ELevel.ERROR);
            return;
          }
          File nativeFile = new File(nativesDirectory, entry.getName());
          try (InputStream is = zipFile.getInputStream(
              entry); FileOutputStream fos = new FileOutputStream(nativeFile)) {
            int bufferSize;
            byte[] buffer = new byte[16384];
            while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
              fos.write(buffer, 0, bufferSize);

              currentExtractSize += bufferSize;
              this.taskMessage = MessageFormat.format(
                  LanguageUtils.getString(LanguageUtils.getBundle(), "gui.string.extractTask"),
                  archiveFile.getName(), (currentExtractSize * 100) / totalExtractSuze);
              this.percentage = 60 + ((currentExtractSize * 20) / totalExtractSuze);
            }
          }
        }
      } catch (FileNotFoundException fnfe) {
        this.setFatalErrorMessage(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.fileNotFound"),
            archiveFile.getName()));
        LoggerUtils.logMessage("Failed to find package file", fnfe, ELevel.ERROR);
      } catch (IOException ioe) {
        this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "gui.exception.io.package.extractFailed"));
        LoggerUtils.logMessage("Failed to extract package files", ioe, ELevel.ERROR);
      } finally {
        if (!archiveFile.delete()) {
          LoggerUtils.logMessage("Failed to delete package file", ELevel.ERROR);
        }
      }
    }
    this.taskMessage = "";
  }

  @Override
  void updateClasspath() {
    EState.setInstance(EState.UPDATE_CLASSPATH);
    this.stateMessage = EState.UPDATE_CLASSPATH.getMessage();
    this.percentage = 85;

    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File[] jarFiles = binDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
      }
    });
    Objects.requireNonNull(jarFiles, "jarFiles == null!");

    URL[] jarUrls = new URL[jarFiles.length + 1];
    try {
      for (int i = 0; i < jarFiles.length; i++) {
        jarUrls[i] = jarFiles[i].toURI().toURL();
      }

      File versionDirectory = new File(VersionUtils.versionsDirectory,
          ConfigUtils.getInstance().getSelectedVersion());
      File minecraftJarFile = new File(versionDirectory,
          MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion()));
      jarUrls[jarFiles.length] = minecraftJarFile.toURI().toURL();

      this.percentage = 85 + ((jarUrls.length * 5) / jarUrls.length);
      LoggerUtils.logMessage(Arrays.toString(jarUrls), ELevel.INFO);
    } catch (MalformedURLException murle) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "gui.exception.io.classpath.updateFailed"));
      LoggerUtils.logMessage("Failed to update classpath", murle, ELevel.ERROR);
      return;
    }

    if (this.minecraftAppletLoader == null) {
      final URL[] finalJarUrls = jarUrls;
      this.minecraftAppletLoader = AccessController.doPrivileged(
          new PrivilegedAction<URLClassLoader>() {
            @Override
            public URLClassLoader run() {
              URLClassLoader urlLoader = new URLClassLoader(finalJarUrls,
                  Thread.currentThread().getContextClassLoader());

              Thread.currentThread().setContextClassLoader(urlLoader);
              return urlLoader;
            }
          });
    }

    File nativesDirectory = new File(binDirectory, "natives");
    this.unloadLibraries(nativesDirectory);
    this.loadLibraries(nativesDirectory);
  }

  @Override
  public void run() {
    EState.setInstance(EState.INIT);
    this.stateMessage = EState.INIT.getMessage();
    this.taskMessage = "";
    this.percentage = 0;

    try {
      EState.setInstance(EState.CHECK_CACHE);
      this.stateMessage = EState.CHECK_CACHE.getMessage();
      this.percentage = 5;

      if (!MinecraftUtils.isGameCached()) {
        this.determinePackage();
        this.downloadPackage();
        this.extractPackage();
      }
      this.updateClasspath();
    } catch (Throwable t) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "gui.throwable.minecraft.updateFailed"));
      LoggerUtils.logMessage("Failed to update Minecraft", t, ELevel.ERROR);
    } finally {
      if (!this.isFatalErrorOccurred()) {
        EState.setInstance(EState.DONE);
        this.stateMessage = EState.DONE.getMessage();
        this.percentage = 95;
      }
    }
  }
}
