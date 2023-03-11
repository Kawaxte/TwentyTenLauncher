package ee.twentyten.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.discord.DiscordRichPresenceUtils;
import ee.twentyten.util.launcher.LauncherUtils;
import ee.twentyten.util.launcher.options.LanguageUtils;
import ee.twentyten.util.launcher.options.VersionUtils;
import ee.twentyten.util.log.LoggerUtils;
import ee.twentyten.util.minecraft.MinecraftUtils;
import ee.twentyten.util.request.ConnectionRequestUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.HttpsURLConnection;

public class MinecraftUpdaterImpl extends MinecraftUpdater implements Runnable {

  public MinecraftUpdaterImpl() {
    EState.setInstance(EState.INIT);
    this.stateMessage = EState.INIT.getMessage();
    this.taskMessage = "";
    this.percentage = 0;
  }

  @Override
  void determinePackage() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    EState.setInstance(EState.DETERMINE_PACKAGE);
    this.stateMessage = EState.DETERMINE_PACKAGE.getMessage();
    this.percentage = 5;

    String fileName;
    try {
      URL[] lwjglJarUrls = new URL[MinecraftUtils.lwjglJars.length];
      for (int i = 0; i < MinecraftUtils.lwjglJars.length; i++) {
        switch (platform) {
          case MACOSX:
            lwjglJarUrls[i] = Objects.equals(SystemUtils.osArch, "aarch64") ? new URL(
                MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                    "aarch64", MinecraftUtils.lwjglJars[i])) : new URL(
                MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                    "amd64", MinecraftUtils.lwjglJars[i]));
            break;
          case LINUX:
            switch (SystemUtils.osArch) {
              case "aarch64":
                lwjglJarUrls[i] = new URL(
                    MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                        "aarch64", MinecraftUtils.lwjglJars[i]));
                break;
              case "aarch32":
                lwjglJarUrls[i] = new URL(
                    MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                        "aarch32", MinecraftUtils.lwjglJars[i]));
                break;
              case "amd64":
                lwjglJarUrls[i] = new URL(
                    MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                        "amd64", MinecraftUtils.lwjglJars[i]));
                break;
              default:
                lwjglJarUrls[i] = new URL(
                    MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                        "x86", MinecraftUtils.lwjglJars[i]));
                break;
            }
            break;
          case WINDOWS:
            lwjglJarUrls[i] = Objects.equals(SystemUtils.osArch, "amd64") ? new URL(
                MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                    "amd64", MinecraftUtils.lwjglJars[i])) : new URL(
                MessageFormat.format("{0}/{1}/{2}", MinecraftUtils.lwjglUrl,
                    "x86", MinecraftUtils.lwjglJars[i]));
            break;
          default:
            break;
        }
      }

      URL lwjglNativesUrl = null;
      switch (platform) {
        case MACOSX:
          lwjglNativesUrl = SystemUtils.osArch.equals("aarch64") ? new URL(
              MessageFormat.format("{0}/{1}/natives-macosx.zip", MinecraftUtils.lwjglUrl,
                  "aarch64")) : new URL(
              MessageFormat.format("{0}/{1}/natives-macosx.zip", MinecraftUtils.lwjglUrl,
                  "amd64"));
          break;
        case LINUX:
          switch (SystemUtils.osArch) {
            case "aarch64":
              lwjglNativesUrl = new URL(
                  MessageFormat.format("{0}/{1}/natives-linux.zip", MinecraftUtils.lwjglUrl,
                      "aarch64"));
              break;
            case "aarch32":
              lwjglNativesUrl = new URL(
                  MessageFormat.format("{0}/{1}/natives-linux.zip", MinecraftUtils.lwjglUrl,
                      "aarch32"));
              break;
            case "amd64":
              lwjglNativesUrl = new URL(
                  MessageFormat.format("{0}/{1}/natives-linux.zip", MinecraftUtils.lwjglUrl,
                      "amd64"));
              break;
            default:
              lwjglNativesUrl = new URL(
                  MessageFormat.format("{0}/{1}/natives-linux.zip", MinecraftUtils.lwjglUrl,
                      "x86"));
              break;
          }
          break;
        case WINDOWS:
          lwjglNativesUrl = Objects.equals(SystemUtils.osArch, "amd64") ? new URL(
              MessageFormat.format("{0}/{1}/natives-windows.zip", MinecraftUtils.lwjglUrl,
                  "amd64")) : new URL(
              MessageFormat.format("{0}/{1}/natives-windows.zip", MinecraftUtils.lwjglUrl,
                  "x86"));
          break;
        default:
          break;
      }
      URL minecraftJarUrl = new URL(
          MessageFormat.format("{0}/{1}.jar", MinecraftUtils.minecraftJarUrl,
              ConfigUtils.getInstance().getSelectedVersion()));
      URL[] packageUrls = {minecraftJarUrl, lwjglNativesUrl};

      List<Future<Integer>> determineFutures = new ArrayList<>();
      ExecutorService determineService = Executors.newFixedThreadPool(packageUrls.length);
      for (final URL packageUrl : packageUrls) {
        Future<Integer> future = determineService.submit(new Callable<Integer>() {
          @Override
          public Integer call() {
            HttpsURLConnection connection = new ConnectionRequest.Builder()
                .setUrl(packageUrl)
                .setMethod(EMethod.HEAD)
                .setHeaders(ConnectionRequestUtils.NO_CACHE)
                .setSSLSocketFactory(ConnectionRequestUtils.getSSLSocketFactory())
                .setUseCaches(false)
                .build().performHttpsRequest();
            return connection.getContentLength();
          }
        });
        determineFutures.add(future);
      }

      Map<String, Integer> contentLengths = new HashMap<>();
      for (int i = 0; i < packageUrls.length; i++) {
        fileName = FileUtils.getFileName(packageUrls[i]);
        try {
          int contentLength = determineFutures.get(i).get();
          contentLengths.put(packageUrls[i].toString(), contentLength);
        } catch (ExecutionException ee) {
          if (contentLengths.get(packageUrls[i].toString()) == -1) {
            this.setFatalErrorMessage(MessageFormat.format(
                LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
                fileName));
            LoggerUtils.logMessage(MessageFormat.format("Failed to find {0}", fileName), ee,
                ELevel.ERROR);
            return;
          }
          LoggerUtils.logMessage("Failed to determine content length", ee, ELevel.ERROR);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          LoggerUtils.logMessage("Interrupted while determining content length", ie, ELevel.ERROR);
        }
      }

      List<URL> urls = new ArrayList<>();
      for (URL lwjglJarUrl : lwjglJarUrls) {
        MinecraftUtils.checkForLwjglJarFiles(urls, lwjglJarUrl);
      }
      MinecraftUtils.checkForLwjglNativeLibraryFiles(platform, urls, lwjglNativesUrl);
      MinecraftUtils.checkForMinecraftJarFile(urls, minecraftJarUrl);

      this.urls = urls.toArray(new URL[0]);
      LoggerUtils.logMessage(Arrays.toString(this.urls), ELevel.INFO);
    } catch (MalformedURLException murle) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.io.package.determineFailed"));
      LoggerUtils.logMessage("Failed to determine package URLs", murle, ELevel.ERROR);
    }
  }

  @Override
  void downloadPackage() {
    int[] fileSizes = new int[this.urls.length];
    int currentDownloadSize = 0;
    int totalDownloadSize = this.getTotalDownloadSize(fileSizes, currentDownloadSize);

    EState.setInstance(EState.DOWNLOAD_PACKAGE);
    this.stateMessage = EState.DOWNLOAD_PACKAGE.getMessage();
    this.percentage = 10;

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

      HttpsURLConnection connection = new ConnectionRequest.Builder()
          .setUrl(fileUrl)
          .setMethod(EMethod.GET)
          .setSSLSocketFactory(ConnectionRequestUtils.getSSLSocketFactory())
          .build().performHttpsRequest();
      try (InputStream is = connection.getInputStream();
          FileOutputStream fos = new FileOutputStream(packageFile)) {
        int bufferSize;
        int downloadedSize = 0;
        byte[] buffer = new byte[65536];
        long downloadStartTime = System.currentTimeMillis();
        String downloadSpeedMessage = "";
        while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
          fos.write(buffer, 0, bufferSize);

          currentDownloadSize += bufferSize;
          this.taskMessage = MessageFormat.format(
              LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.downloadTask"),
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
            LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
            packageFile.getName()));
        LoggerUtils.logMessage("Failed to find package file", fnfe, ELevel.ERROR);
        return;
      } catch (IOException ioe) {
        this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "mui.exception.io.package.downloadFailed"));
        LoggerUtils.logMessage("Failed to download package files", ioe, ELevel.ERROR);
        return;
      }
    }
    this.taskMessage = "";
  }

  @Override
  void extractPackage() {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File[] archiveFiles = binDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".zip");
      }
    });
    Objects.requireNonNull(archiveFiles, "packageFiles == null!");

    int currentExtractSize = 0;
    int totalExtractSize = this.getTotalExtractSize(archiveFiles, currentExtractSize);

    EState.setInstance(EState.EXTRACT_PACKAGE);
    this.stateMessage = EState.EXTRACT_PACKAGE.getMessage();
    this.percentage = 60;

    for (File archiveFile : archiveFiles) {
      try (ZipFile zipFile = new ZipFile(archiveFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (!entry.isDirectory() && entry.getName().indexOf(47) != -1) {
            continue;
          }

          File nativesDirectory = new File(binDirectory, "natives");
          if (!nativesDirectory.mkdirs() && !nativesDirectory.exists()) {
            LoggerUtils.logMessage("Failed to create natives directory", ELevel.ERROR);
            return;
          }

          File libraryName = new File(nativesDirectory, entry.getName());
          if (libraryName.exists() && !libraryName.delete()) {
            continue;
          }

          try (InputStream is = zipFile.getInputStream(entry);
              FileOutputStream fos = new FileOutputStream(libraryName)) {
            int bufferSize;
            byte[] buffer = new byte[65536];
            while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
              fos.write(buffer, 0, bufferSize);

              currentExtractSize += bufferSize;
              this.taskMessage = MessageFormat.format(
                  LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.extractTask"),
                  entry.getName(), (currentExtractSize * 100) / totalExtractSize);
              this.percentage = 60 + ((currentExtractSize * 20) / totalExtractSize);
            }
          }
        }
      } catch (FileNotFoundException fnfe) {
        this.setFatalErrorMessage(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.fileNotFound"),
            archiveFile.getName()));
        LoggerUtils.logMessage("Failed to find package file", fnfe, ELevel.ERROR);
        return;
      } catch (IOException ioe) {
        this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
            "mui.exception.io.package.extractFailed"));
        LoggerUtils.logMessage("Failed to extract package files", ioe, ELevel.ERROR);
        return;
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
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    File[] jarFiles = binDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
      }
    });
    Objects.requireNonNull(jarFiles, "jarFiles == null!");

    EState.setInstance(EState.UPDATE_CLASSPATH);
    this.stateMessage = EState.UPDATE_CLASSPATH.getMessage();
    this.percentage = 85;

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
          "mui.exception.io.classpath.updateFailed"));
      LoggerUtils.logMessage("Failed to update classpath", murle, ELevel.ERROR);
      return;
    }

    if (this.minecraftApplet == null) {
      final URL[] finalJarUrls = jarUrls;
      this.minecraftApplet = AccessController.doPrivileged(
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
    this.loadLibraries(nativesDirectory);
  }

  @Override
  public void run() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    EState.setInstance(EState.CHECK_CACHE);
    this.stateMessage = EState.CHECK_CACHE.getMessage();
    this.percentage = 5;

    try {
      if (!this.isMinecraftCached(platform)) {
        DiscordRichPresenceUtils.updateRichPresence("Updating Minecraft",
            MessageFormat.format("{0} | {1}",
                MinecraftUtils.getInstance().getUsername(), MinecraftUtils.getVersion()));

        this.determinePackage();
        this.downloadPackage();
        this.extractPackage();
      }
    } catch (Throwable t) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.throwable.minecraft.updateFailed"));
      LoggerUtils.logMessage("Failed to update Minecraft", t, ELevel.ERROR);
    } finally {
      if (!this.isFatalErrorOccurred()) {
        this.updateClasspath();

        EState.setInstance(EState.DONE);
        this.stateMessage = EState.DONE.getMessage();
        this.percentage = 95;
      }
    }
  }
}
