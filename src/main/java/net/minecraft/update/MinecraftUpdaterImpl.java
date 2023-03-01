package net.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELoggerLevel;
import ee.twentyten.request.ERequestHeader;
import ee.twentyten.request.ERequestMethod;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import ee.twentyten.util.VersionUtils;
import java.applet.Applet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;
import net.minecraft.util.MinecraftUtils;

public class MinecraftUpdaterImpl extends MinecraftUpdater implements Runnable {

  private URL[] packageUrls;
  @Getter
  private String updateStateMessage;
  @Getter
  private String updateTaskMessage;
  @Getter
  private int updatePercentage;
  @Getter
  private boolean isFatalErrorOccurred;
  private boolean isNativesLoaded;
  private ClassLoader minecraftAppletLoader;

  public Applet createMinecraftAppletInstance() {
    try {
      return (Applet) this.minecraftAppletLoader.loadClass("net.minecraft.client.MinecraftApplet")
          .newInstance();
    } catch (InstantiationException ie) {
      this.showFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.instantation.class"),
          "MinecraftApplet"));
      LoggerUtils.log("Failed to instantiate MinecraftApplet class", ie, ELoggerLevel.ERROR);
    } catch (IllegalAccessException iae) {
      this.showFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.illegalAccess.class"),
          "MinecraftApplet"));
      LoggerUtils.log("Failed to access MinecraftApplet class", iae, ELoggerLevel.ERROR);
    } catch (ClassNotFoundException cnfe) {
      this.showFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.classNotFound"),
          "MinecraftApplet"));
      LoggerUtils.log("Failed to find MinecraftApplet class", cnfe, ELoggerLevel.ERROR);
    }
    return null;
  }

  public void showFatalErrorMessage(String message) {
    this.isFatalErrorOccurred = true;

    this.updateStateMessage = MessageFormat.format(
        LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.fatalErrorMessage"),
        EState.getInstance().ordinal(), message);
    this.updateTaskMessage = "";
  }

  private boolean isContentLengthNotZero(URL url, String urlString) {
    if (FileUtils.getContentLength(url) == -1) {
      Throwable fnfe = new FileNotFoundException(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
          urlString));
      this.showFatalErrorMessage(fnfe.getMessage());
      LoggerUtils.log("Failed to check content length", fnfe, ELoggerLevel.ERROR);
      return true;
    }
    return false;
  }

  private void unloadNativeLibraries() {
    if (this.isNativesLoaded) {
      return;
    }

    try {
      Field declaredField = ClassLoader.class.getDeclaredField("loadedLibraryNames");
      declaredField.setAccessible(true);

      Vector<?> libraries = (Vector<?>) declaredField.get(this.getClass().getClassLoader());
      libraries.clear();
    } catch (NoSuchFieldException nsfe) {
      this.showFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fieldNotFound"),
          "loadedLibraryNames"));
      LoggerUtils.log("Failed to find loadedLibraryNames field", nsfe, ELoggerLevel.ERROR);
    } catch (IllegalAccessException iae) {
      this.showFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.illegalAccess.field"),
          "loadedLibraryNames"));
      LoggerUtils.log("Failed to access loadedLibraryNames field", iae, ELoggerLevel.ERROR);
    }
  }

  private void loadNativeLibraries(File directory) {
    String[] libraryPaths = new String[]{"org.lwjgl.librarypath",
        "net.java.games.input.librarypath"};
    for (String libraryPath : libraryPaths) {
      File nativesDirectory = new File(directory, "natives");
      System.setProperty(libraryPath, nativesDirectory.getAbsolutePath());
      LoggerUtils.log(MessageFormat.format("{0}={1}", libraryPath, System.getProperty(libraryPath)),
          ELoggerLevel.INFO);
    }
    this.isNativesLoaded = true;
  }

  @Override
  public void initLoader() {
    EState.setInstance(EState.INIT_STATE);
    this.updateStateMessage = EState.INIT_STATE.getMessage();
    this.updateTaskMessage = "";
    this.updatePercentage = 0;
  }

  @Override
  public void determinePackages() {
    EState.setInstance(EState.DETERMINE_STATE);
    this.updateStateMessage = EState.DETERMINE_STATE.getMessage();
    this.updatePercentage = 5;

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");
    try {
      List<URL> packageUrls = new ArrayList<>();

      boolean isJarUrlNotZero;
      for (String lwjglJar : MinecraftUtils.lwjglJars) {
        URL lwjglJarUrl = new URL(
            MessageFormat.format("{0}/{1}", MinecraftUtils.lwjglUrl, lwjglJar));
        isJarUrlNotZero = this.isContentLengthNotZero(lwjglJarUrl,
            FileUtils.getFileNameFromUrl(lwjglJarUrl));
        if (isJarUrlNotZero) {
          return;
        }
        MinecraftUtils.getLwjglJarFile(packageUrls, lwjglJarUrl);
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

      boolean isLwjglNativesUrlNotZero = this.isContentLengthNotZero(lwjglNativesUrl,
          FileUtils.getFileNameFromUrl(lwjglNativesUrl));
      if (isLwjglNativesUrlNotZero) {
        return;
      }
      switch (platform) {
        case MACOSX:
          MinecraftUtils.getLwjglNativeLibraries(packageUrls, lwjglNativesUrl,
              MinecraftUtils.lwjglMacosxNatives);
          break;
        case LINUX:
          MinecraftUtils.getLwjglNativeLibraries(packageUrls, lwjglNativesUrl,
              MinecraftUtils.lwjglLinuxNatives);
          break;
        case WINDOWS:
          MinecraftUtils.getLwjglNativeLibraries(packageUrls, lwjglNativesUrl,
              MinecraftUtils.lwjglWindowsNatives);
          break;
        default:
          throw new IllegalStateException(String.valueOf(platform));
      }

      URL minecraftJarUrl = new URL(MessageFormat.format("{0}/{1}", MinecraftUtils.minecraftJarUrl,
          MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion())));
      boolean isMinecraftClientUrlNotZero = this.isContentLengthNotZero(minecraftJarUrl,
          FileUtils.getFileNameFromUrl(minecraftJarUrl));
      if (isMinecraftClientUrlNotZero) {
        return;
      }
      MinecraftUtils.getMinecraftJarFile(packageUrls, minecraftJarUrl);

      this.packageUrls = packageUrls.toArray(new URL[0]);
      LoggerUtils.log(Arrays.toString(this.packageUrls), ELoggerLevel.INFO);
    } catch (MalformedURLException murle) {
      this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.io.package.determineFailed"));
      LoggerUtils.log("Failed to determine package URLs", murle, ELoggerLevel.ERROR);
    }
  }

  @Override
  public int retrievePackages(int[] sizes, int size) {
    EState.setInstance(EState.RETRIEVE_STATE);
    this.updateStateMessage = EState.RETRIEVE_STATE.getMessage();

    List<Callable<Integer>> retrieveTasks = new ArrayList<>();
    for (final URL fileUrl : this.packageUrls) {
      retrieveTasks.add(new Callable<Integer>() {
        @Override
        public Integer call() {
          HttpsURLConnection connection = RequestUtils.performHttpsRequest(fileUrl,
              ERequestMethod.HEAD, ERequestHeader.NO_CACHE);
          Objects.requireNonNull(connection, "connection == null!");
          try {
            return connection.getContentLength();
          } finally {
            connection.disconnect();
          }
        }
      });
    }

    ExecutorService retrieveService = Executors.newFixedThreadPool(this.packageUrls.length);
    try {
      List<Future<Integer>> retrieveFutures = retrieveService.invokeAll(retrieveTasks);
      for (int i = 0; i < retrieveFutures.size(); i++) {
        Future<Integer> retrieveFuture = retrieveFutures.get(i);

        int fileSize = retrieveFuture.get();
        sizes[i] = fileSize;
        size += fileSize;
        this.updatePercentage = 5 + (((i + 1) * 5) / this.packageUrls.length);
      }
    } catch (ExecutionException ee) {
      this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.io.package.retrieveFailed"));
      LoggerUtils.log("Failed to retrieve package sizes", ee, ELoggerLevel.ERROR);
    } catch (InterruptedException ie) {
      LoggerUtils.log("Interrupted while retrieving package sizes", ie, ELoggerLevel.ERROR);
    } finally {
      retrieveService.shutdown();
    }
    return size;
  }

  @Override
  public void downloadPackages() {
    int[] fileSizes = new int[this.packageUrls.length];
    int currentFileSize = 0;
    int totalFileSize = this.retrievePackages(fileSizes, currentFileSize);

    EState.setInstance(EState.DOWNLOAD_STATE);
    this.updateStateMessage = EState.DOWNLOAD_STATE.getMessage();
    this.updatePercentage = 10;
    for (URL fileUrl : this.packageUrls) {
      File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
      if (!binDirectory.mkdirs() && !binDirectory.exists()) {
        LoggerUtils.log("Failed to create bin directory", ELoggerLevel.ERROR);
        return;
      }

      File packageFile = new File(binDirectory, FileUtils.getFileNameFromUrl(fileUrl));
      if (Objects.equals(packageFile.getName(),
          MessageFormat.format("{0}.jar", ConfigUtils.getInstance().getSelectedVersion()))) {
        File versionDirectory = new File(VersionUtils.versionsDirectory,
            ConfigUtils.getInstance().getSelectedVersion());
        if (!versionDirectory.mkdirs() && !versionDirectory.exists()) {
          LoggerUtils.log("Failed to create version directory", ELoggerLevel.ERROR);
          return;
        }
        packageFile = new File(versionDirectory, packageFile.getName());
      }

      HttpsURLConnection connection = RequestUtils.performHttpsRequest(fileUrl, ERequestMethod.GET,
          ERequestHeader.NO_CACHE);
      Objects.requireNonNull(connection, "connection == null!");
      try (InputStream is = connection.getInputStream(); FileOutputStream fos = new FileOutputStream(
          packageFile)) {
        int bufferSize;
        int downloadedFileSize = 0;
        byte[] buffer = new byte[65536];
        long downloadStartTime = System.currentTimeMillis();
        String downloadSpeedMessage = "";
        while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
          fos.write(buffer, 0, bufferSize);

          currentFileSize += bufferSize;
          this.updateTaskMessage = MessageFormat.format(
              LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.subtaskDownload"),
              FileUtils.getFileNameFromUrl(fileUrl), (currentFileSize * 100) / totalFileSize);
          this.updatePercentage = 10 + ((currentFileSize * 45) / totalFileSize);

          downloadedFileSize += bufferSize;
          long downloadElapsedTime = System.currentTimeMillis() - downloadStartTime;
          if (downloadElapsedTime >= 1000L) {
            double downloadSpeed = downloadedFileSize / (double) downloadElapsedTime;
            downloadSpeed = (downloadSpeed * 100.0d) / 100.0d;
            downloadSpeedMessage = MessageFormat.format(" @ {0,number,#.##} KB/sec", downloadSpeed);

            downloadedFileSize = 0;
            downloadStartTime += 1000L;
          }
          this.updateTaskMessage = this.updateTaskMessage.concat(downloadSpeedMessage);
        }
      } catch (FileNotFoundException fnfe) {
        this.showFatalErrorMessage(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
            packageFile.getName()));
        LoggerUtils.log("Failed to find package file", fnfe, ELoggerLevel.ERROR);
      } catch (IOException ioe) {
        this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "mui.exception.io.package.downloadFailed"));
        LoggerUtils.log("Failed to download package files", ioe, ELoggerLevel.ERROR);
      } finally {
        connection.disconnect();
      }
    }
    this.updateTaskMessage = "";
  }

  @Override
  public void extractDownloadedPackages() {
    EState.setInstance(EState.EXTRACT_STATE);
    this.updateStateMessage = EState.EXTRACT_STATE.getMessage();
    this.updatePercentage = 60;

    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File[] archiveFiles = binDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".zip");
      }
    });
    Objects.requireNonNull(archiveFiles, "packageFiles == null!");

    int currentArchiveSize = 0;
    int totalArchiveSize = 0;
    for (File archiveFile : archiveFiles) {
      try (ZipFile zipFile = new ZipFile(archiveFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (entry.isDirectory()) {
            continue;
          }
          totalArchiveSize += entry.getSize();

          File nativesDirectory = new File(binDirectory, "natives");
          if (!nativesDirectory.mkdirs() && !nativesDirectory.exists()) {
            LoggerUtils.log("Failed to create natives directory", ELoggerLevel.ERROR);
            return;
          }
          File nativeFile = new File(nativesDirectory, entry.getName());
          try (InputStream is = zipFile.getInputStream(
              entry); FileOutputStream fos = new FileOutputStream(nativeFile)) {
            int bufferSize;
            byte[] buffer = new byte[65536];
            while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
              fos.write(buffer, 0, bufferSize);

              currentArchiveSize += bufferSize;
              this.updateTaskMessage = MessageFormat.format(
                  LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.subtaskExtract"),
                  archiveFile.getName(), (currentArchiveSize * 100) / totalArchiveSize);
              this.updatePercentage = 60 + ((currentArchiveSize * 20) / totalArchiveSize);
            }
          }
        }
      } catch (FileNotFoundException fnfe) {
        this.showFatalErrorMessage(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
            archiveFile.getName()));
        LoggerUtils.log("Failed to find package file", fnfe, ELoggerLevel.ERROR);
      } catch (IOException ioe) {
        this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "mui.exception.io.package.extractFailed"));
        LoggerUtils.log("Failed to extract package files", ioe, ELoggerLevel.ERROR);
      } finally {
        if (!archiveFile.delete()) {
          LoggerUtils.log("Failed to delete package file", ELoggerLevel.ERROR);
        }
      }
    }
    this.updateTaskMessage = "";
  }

  @Override
  public void updateClasspath() {
    EState.setInstance(EState.UPDATE_STATE);
    this.updateStateMessage = EState.UPDATE_STATE.getMessage();
    this.updatePercentage = 85;

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

      this.updatePercentage = 85 + ((jarUrls.length * 5) / jarUrls.length);
      LoggerUtils.log(Arrays.toString(jarUrls), ELoggerLevel.INFO);
    } catch (MalformedURLException murle) {
      this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.io.classpath.updateFailed"));
      LoggerUtils.log("Failed to update classpath", murle, ELoggerLevel.ERROR);
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
    this.unloadNativeLibraries();
    this.loadNativeLibraries(binDirectory);
  }

  @Override
  public void run() {
    this.initLoader();

    EState.setInstance(EState.CACHE_STATE);
    this.updateStateMessage = EState.CACHE_STATE.getMessage();
    try {
      boolean isCached = MinecraftUtils.isMinecraftCached();
      this.updatePercentage = 5;
      if (!isCached) {
        this.determinePackages();
        this.downloadPackages();
        this.extractDownloadedPackages();
      }
      this.updateClasspath();
    } catch (Throwable t) {
      this.showFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.t.minecraft.updateFailed"));
      LoggerUtils.log("Failed to update Minecraft", t, ELoggerLevel.ERROR);
    } finally {
      EState.setInstance(EState.DONE_STATE);
      this.updateStateMessage = EState.DONE_STATE.getMessage();
      this.updatePercentage = 95;
    }
  }
}
