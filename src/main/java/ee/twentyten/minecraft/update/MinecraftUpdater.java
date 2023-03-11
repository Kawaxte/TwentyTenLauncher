package ee.twentyten.minecraft.update;

import ee.twentyten.EPlatform;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.launcher.LauncherUtils;
import ee.twentyten.util.launcher.options.LanguageUtils;
import ee.twentyten.util.log.LoggerUtils;
import ee.twentyten.util.minecraft.MinecraftUtils;
import ee.twentyten.util.request.ConnectionRequestUtils;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;

@Getter
abstract class MinecraftUpdater {

  public String stateMessage;
  public String taskMessage;
  public int percentage;
  public boolean isFatalErrorOccurred;
  boolean isLibrariesLoaded;
  URL[] urls;
  ClassLoader minecraftApplet;

  int getTotalDownloadSize(int[] sizes, int size) {
    List<Callable<Integer>> retrieveTasks = new ArrayList<>();

    final HttpsURLConnection[] connection = {null};
    for (final URL fileUrl : this.urls) {
      retrieveTasks.add(new Callable<Integer>() {
        @Override
        public Integer call() {
          connection[0] = new ConnectionRequest.Builder()
              .setUrl(fileUrl)
              .setMethod(EMethod.HEAD)
              .setHeaders(ConnectionRequestUtils.NO_CACHE)
              .setSSLSocketFactory(ConnectionRequestUtils.getSSLSocketFactory())
              .setUseCaches(false)
              .build().performHttpsRequest();
          return connection[0].getContentLength();
        }
      });
    }

    ExecutorService retrieveService = Executors.newFixedThreadPool(this.urls.length);
    try {
      List<Future<Integer>> retrieveFutures = retrieveService.invokeAll(retrieveTasks);
      for (int i = 0; i < retrieveFutures.size(); i++) {
        Future<Integer> retrieveFuture = retrieveFutures.get(i);

        int fileSize = retrieveFuture.get();
        sizes[i] = fileSize;
        size += fileSize;
        this.percentage = 5 + ((i / retrieveFutures.size()) * 5);
      }
    } catch (ExecutionException ee) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "mui.exception.io.package.retrieveFailed"));
      LoggerUtils.logMessage("Failed to retrieve package sizes", ee, ELevel.ERROR);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      LoggerUtils.logMessage("Interrupted while retrieving package sizes", ie, ELevel.ERROR);
    } finally {
      retrieveService.shutdown();
    }
    return size;
  }

  int getTotalExtractSize(File[] files, int size) {
    for (File file : files) {
      try (ZipFile zipFile = new ZipFile(file)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (!entry.isDirectory() && entry.getName().indexOf(47) != -1) {
            continue;
          }
          size += entry.getSize();
        }
      } catch (IOException ioe) {
        LoggerUtils.logMessage("Failed to get total extract size", ioe, ELevel.ERROR);
      }
    }
    return size;
  }

  void setFatalErrorMessage(String message) {
    this.isFatalErrorOccurred = true;

    this.stateMessage = MessageFormat.format(
        LanguageUtils.getString(LanguageUtils.getBundle(), "mui.string.fatalErrorMessage"),
        EState.getInstance().ordinal(), message);
    this.taskMessage = "";
  }

  boolean isMinecraftCached(EPlatform platform) {
    File binDirectory = new File(LauncherUtils.workingDirectory, "bin");
    Objects.requireNonNull(binDirectory, "binDirectory == null!");
    if (this.isFilesExistInPath(MinecraftUtils.lwjglJars, binDirectory)) {
      return false;
    }

    File nativesDirectory = new File(binDirectory, "natives");
    Objects.requireNonNull(nativesDirectory, "nativesDirectory == null!");

    switch (platform) {
      case MACOSX:
        if (Objects.equals(SystemUtils.osArch, "aarch64")) {
          if (this.isFilesExistInPath(MinecraftUtils.lwjglMacosxNativesForAARCH64,
              nativesDirectory)) {
            return false;
          }
        } else {
          if (this.isFilesExistInPath(MinecraftUtils.lwjglMacosxNativesForAMD64,
              nativesDirectory)) {
            return false;
          }
        }
        break;
      case LINUX:
        switch (SystemUtils.osArch) {
          case "aarch64":
            if (this.isFilesExistInPath(MinecraftUtils.LwjglLinuxNativesForAARCH64,
                nativesDirectory)) {
              return false;
            }
            break;
          case "aarch32":
            if (this.isFilesExistInPath(MinecraftUtils.lwjglLinuxNativesForAARCH32,
                nativesDirectory)) {
              return false;
            }
            break;
          case "amd64":
            if (this.isFilesExistInPath(MinecraftUtils.lwjglLinuxNativesForAMD64,
                nativesDirectory)) {
              return false;
            }
            break;
          default:
            if (this.isFilesExistInPath(MinecraftUtils.lwjglLinuxNativesForX86, nativesDirectory)) {
              return false;
            }
            break;
        }
        break;
      case WINDOWS:
        if (Objects.equals(SystemUtils.osArch, "amd64")) {
          if (this.isFilesExistInPath(MinecraftUtils.lwjglWindowsNativesForAMD64,
              nativesDirectory)) {
            return false;
          }
        } else {
          if (this.isFilesExistInPath(MinecraftUtils.lwjglWindowsNativesForX86, nativesDirectory)) {
            return false;
          }
        }
        break;
      default:
        throw new IllegalStateException(String.valueOf(platform));
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

  private boolean isFilesExistInPath(String[] lwjglJars, File binDirectory) {
    for (String lwjglFile : lwjglJars) {
      if (!Files.exists(Paths.get(String.valueOf(binDirectory), lwjglFile))) {
        return true;
      }
    }
    return false;
  }

  public Applet loadMinecraftApplet() {
    try {
      return (Applet) this.minecraftApplet.loadClass("net.minecraft.client.MinecraftApplet")
          .newInstance();
    } catch (InstantiationException ie) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.instantation.class"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to instantiate MinecraftApplet class", ie, ELevel.ERROR);
    } catch (IllegalAccessException iae) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.illegalAccess.class"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to access MinecraftApplet class", iae, ELevel.ERROR);
    } catch (ClassNotFoundException cnfe) {
      this.setFatalErrorMessage(MessageFormat.format(
          LanguageUtils.getString(LanguageUtils.getBundle(), "mui.exception.classNotFound"),
          "MinecraftApplet"));
      LoggerUtils.logMessage("Failed to find MinecraftApplet class", cnfe, ELevel.ERROR);
    }
    return null;
  }

  void loadLibraries(File directory) {
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

  abstract void determinePackage();

  abstract void downloadPackage();

  abstract void extractPackage();

  abstract void updateClasspath();
}
