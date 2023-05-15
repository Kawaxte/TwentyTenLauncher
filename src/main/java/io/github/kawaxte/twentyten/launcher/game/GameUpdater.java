package io.github.kawaxte.twentyten.launcher.game;

import io.github.kawaxte.twentyten.launcher.EPlatform;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.val;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GameUpdater {

  private static final Path versionDirectoryPath;
  private static final Logger LOGGER;
  private static final Path binDirectoryPath;
  private static final Path nativesDirectoryPath;
  private static final Path versionsDirectoryPath;

  static {
    LOGGER = LogManager.getLogger(GameUpdater.class);

    binDirectoryPath = Paths.get(LauncherUtils.workingDirectoryPath.toString(), "bin");
    nativesDirectoryPath = Paths.get(binDirectoryPath.toString(), "natives");
    val nativesDirectory = nativesDirectoryPath.toFile();
    if (!nativesDirectory.exists() && !nativesDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", nativesDirectoryPath);
    }

    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    versionsDirectoryPath = Paths.get(LauncherUtils.workingDirectoryPath.toString(), "versions");
    versionDirectoryPath = Paths.get(versionsDirectoryPath.toString(), selectedVersion);
    val versionDirectory = versionDirectoryPath.toFile();
    if (!versionDirectory.exists() && !versionDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", versionDirectoryPath);
    }
  }

  private GameUpdater() {}

  public static boolean isGameCached() {
    return isLWJGLJarExistent() && isLWJGLNativeExistent() && isClientJarExistent();
  }

  private static boolean isLWJGLJarExistent() {
    val lwjglJars = getListOfLWJGLJars();
    return lwjglJars.stream()
        .allMatch(lwjglJar -> Files.exists(binDirectoryPath.resolve(lwjglJar)));
  }

  private static boolean isLWJGLNativeExistent() {
    val lwjglNatives =
        Optional.ofNullable(getListLWJGLNatives())
            .orElseThrow(() -> new NullPointerException("lwjglNatives cannot be null"));
    return lwjglNatives.stream()
        .findFirst()
        .filter(lwjglNative -> Files.exists(nativesDirectoryPath.resolve(lwjglNative)))
        .isPresent();
  }

  private static boolean isClientJarExistent() {
    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    val clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
    return Files.exists(versionDirectoryPath.resolve(clientJar));
  }

  private static List<String> getListOfLWJGLJars() {
    return Arrays.asList("jinput.jar", "jutils.jar", "lwjgl.jar", "lwjgl_util.jar");
  }

  private static List<String> getListLWJGLNatives() {
    if (EPlatform.isLinux()) {
      return EPlatform.isAARCH64() || EPlatform.isAMD64()
          ? Arrays.asList("libjinput-linux64.so", "liblwjgl64.so", "libopenal64.so")
          : Arrays.asList("libjinput-linux.so", "liblwjgl.so", "libopenal.so");
    }
    if (EPlatform.isMacOS()) {
      return EPlatform.isAARCH64()
          ? Arrays.asList("liblwjgl.dylib", "openal.dylib")
          : Arrays.asList("libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib");
    }
    if (EPlatform.isWindows()) {
      return EPlatform.isAMD64()
          ? Arrays.asList(
              "jinput-dx8_64.dll",
              "jinput-raw_64.dll",
              "jinput-wintab.dll",
              "lwjgl64.dll",
              "OpenAL64.dll")
          : Arrays.asList(
              "jinput-dx8.dll", "jinput-raw.dll", "jinput-wintab.dll", "lwjgl.dll", "OpenAL32.dll");
    }
    return null;
  }

  private static URL[] getClientUrls() {
    val urls = new URL[3];
    try {
      urls[0] =
          new URL(
              new StringBuilder()
                  .append("https://github.com/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("raw/")
                  .append("nightly/")
                  .append("bin/")
                  .append("client/")
                  .append("legacy_beta/")
                  .toString());
      urls[1] =
          new URL(
              new StringBuilder()
                  .append("https://github.com/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("raw/")
                  .append("nightly/")
                  .append("bin/")
                  .append("client/")
                  .append("legacy_alpha/")
                  .toString());
      urls[2] =
          new URL(
              new StringBuilder()
                  .append("https://github.com/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("raw/")
                  .append("nightly/")
                  .append("bin/")
                  .append("client/")
                  .append("legacy_infdev/")
                  .toString());
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL for Minecraft client(s)", murle);
    }
    return urls;
  }

  private static URL getLWJGLUrls() {
    try {
      if (EPlatform.isAARCH64()) {
        return new URL(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("TwentyTenLauncher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("lwjgl/")
                .append("aarch64/")
                .toString());
      }
      if (EPlatform.isAMD64()) {
        return new URL(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("TwentyTenLauncher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("lwjgl/")
                .append("amd64/")
                .toString());
      }
      if (EPlatform.isX86()) {
        return new URL(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("TwentyTenLauncher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("lwjgl/")
                .append("x86/")
                .toString());
      }
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL for LWJGL", murle);
    }
    return null;
  }

  public static URL[] getUrls() {
    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    val clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();

    val lwjglNativesZips =
        new String[] {"natives-linux.zip", "natives-macosx.zip", "natives-windows.zip"};
    val lwjglJars = getListOfLWJGLJars();
    val clientUrls = getClientUrls();
    val lwjglUrls = getLWJGLUrls();
    val urls = new URL[6];
    try {
      if (!isLWJGLJarExistent()) {
        for (int i = 0; i < lwjglJars.size(); i++) {
          urls[i] = new URL(getLWJGLUrls(), lwjglJars.get(i));
        }
      }
      if (!isLWJGLNativeExistent()) {
        if (EPlatform.isLinux()) {
          urls[4] = new URL(lwjglUrls, lwjglNativesZips[0]);
        }
        if (EPlatform.isMacOS()) {
          urls[4] = new URL(lwjglUrls, lwjglNativesZips[1]);
        }
        if (EPlatform.isWindows()) {
          urls[4] = new URL(lwjglUrls, lwjglNativesZips[2]);
        }
      }
      if (!isClientJarExistent()) {
        if (selectedVersion.startsWith("b")) {
          urls[5] = new URL(clientUrls[0], clientJar);
        }
        if (selectedVersion.startsWith("a")) {
          urls[5] = new URL(clientUrls[1], clientJar);
        }
        if (selectedVersion.startsWith("inf")) {
          urls[5] = new URL(clientUrls[2], clientJar);
        }
      }
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL(s)", murle);
    }
    return Arrays.stream(urls).filter(Objects::nonNull).toArray(URL[]::new);
  }

  public static void downloadPackages(URL[] urls) {
    if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
      GameAppletWrapper.instance.setTaskState(EState.DOWNLOAD_PACKAGES.ordinal());
      GameAppletWrapper.instance.setTaskStateMessage(EState.DOWNLOAD_PACKAGES.getMessage());
      GameAppletWrapper.instance.setTaskProgressMessage(null);
      GameAppletWrapper.instance.setTaskProgress(10);
    }

    val javaIoTmpdir = System.getProperty("java.io.tmpdir");
    val javaIoTmpDirPath = Paths.get(javaIoTmpdir);

    int currentDownloadSize = 0;
    int totalDownloadSize = calcTotalDownloadSize(urls);
    for (val url : urls) {
      try {
        val request = Request.get(url.toURI()).execute();
        val content = request.returnContent();

        val fileNameIndex = url.toString().lastIndexOf("/") + 1;
        val fileName = url.toString().substring(fileNameIndex);
        val file = javaIoTmpDirPath.resolve(fileName).toFile();
        try (val bis = new BufferedInputStream(content.asStream());
            val fos = new FileOutputStream(file)) {
          val buffer = new byte[65536];
          int bytesRead;
          while ((bytesRead = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
            currentDownloadSize += bytesRead;

            int downloadProgress = (currentDownloadSize * 100) / totalDownloadSize;
            int progress = 10 + ((currentDownloadSize * 45) / totalDownloadSize);
            GameAppletWrapper.instance.setTaskProgressMessage(
                "gaw.taskProgressMessage", fileName, downloadProgress);
            GameAppletWrapper.instance.setTaskProgress(progress);
          }
        }
      } catch (FileNotFoundException fnfe) {
        displayErrorMessage(fnfe.getMessage());

        LOGGER.error("Cannot find {}", url.toString(), fnfe);
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot download {}", url.toString(), ioe);
      } catch (URISyntaxException urise) {
        displayErrorMessage(urise.getMessage());

        LOGGER.error("Cannot parse {} as URI", url.toString(), urise);
      }
    }

    moveDownloadedPackages(javaIoTmpDirPath);
  }

  private static void moveDownloadedPackages(Path p) {
    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    val clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
    val clientJarFile = p.resolve(clientJar).toFile();
    val clientJarFileDest = versionDirectoryPath.resolve(clientJar).toFile();
    try {
      if (clientJarFile.exists()) {
        Files.move(clientJarFile.toPath(), clientJarFileDest.toPath());
      }
    } catch (IOException ioe) {
      displayErrorMessage(ioe.getMessage());

      LOGGER.error("Cannot move {} to {}", clientJarFile, clientJarFileDest, ioe);
      return;
    }

    val zipFiles =
        p.toFile().listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
    if (Objects.nonNull(zipFiles)) {
      for (val zipFile : zipFiles) {
        val lwjglNativesZipFile = p.resolve(zipFile.getName()).toFile();
        val lwjglNativesZipFileDest = nativesDirectoryPath.resolve(zipFile.getName()).toFile();
        try {
          if (lwjglNativesZipFile.exists()) {
            Files.move(zipFile.toPath(), lwjglNativesZipFileDest.toPath());
          }
        } catch (IOException ioe) {
          displayErrorMessage(ioe.getMessage());

          LOGGER.error("Cannot move {} to {}", zipFile, lwjglNativesZipFileDest, ioe);
          return;
        }
      }
    }

    val jarFiles = getListOfLWJGLJars();
    for (val jarFile : jarFiles) {
      val lwjglJarFile = p.resolve(jarFile).toFile();
      val lwjglJarFileDest = binDirectoryPath.resolve(jarFile).toFile();
      try {
        if (lwjglJarFile.exists()) {
          Files.move(lwjglJarFile.toPath(), lwjglJarFileDest.toPath());
        }
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot move {} to {}", lwjglJarFile, lwjglJarFileDest, ioe);
        return;
      }
    }
  }

  public static void extractDownloadedPackages() {
    if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
      GameAppletWrapper.instance.setTaskState(EState.EXTRACT_PACKAGES.ordinal());
      GameAppletWrapper.instance.setTaskStateMessage(EState.EXTRACT_PACKAGES.getMessage());
      GameAppletWrapper.instance.setTaskProgressMessage(null);
      GameAppletWrapper.instance.setTaskProgress(55);
    }

    val lwjglNativesZipFiles =
        nativesDirectoryPath
            .toFile()
            .listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
    if (Objects.isNull(lwjglNativesZipFiles)) {
      return;
    }

    int totalExtractSize = calcTotalExtractSize(lwjglNativesZipFiles);
    int currentExtractSize = 0;
    for (val lwjglNativesZipFile : lwjglNativesZipFiles) {
      try (val fis = new FileInputStream(lwjglNativesZipFile);
          val bis = new BufferedInputStream(fis);
          val zis = new ZipInputStream(bis)) {
        ZipEntry entry;
        while (Objects.nonNull(entry = zis.getNextEntry())) {
          val name = entry.getName();
          if (!entry.isDirectory() && name.indexOf(47) != -1) {
            continue;
          }

          val nativeFile = nativesDirectoryPath.resolve(name).toFile();
          try (val fos = new FileOutputStream(nativeFile)) {
            val buffer = new byte[65536];
            int bufferSize;
            while ((bufferSize = zis.read(buffer, 0, buffer.length)) != -1) {
              fos.write(buffer, 0, bufferSize);
              currentExtractSize += bufferSize;

              int extractProgress = (currentExtractSize * 100) / totalExtractSize;
              int progress = 55 + ((currentExtractSize * 20) / totalExtractSize);
              GameAppletWrapper.instance.setTaskProgressMessage(
                  "gaw.taskProgressMessage", name, extractProgress);
              GameAppletWrapper.instance.setTaskProgress(progress);
            }
          }
        }
      } catch (FileNotFoundException fnfe) {
        displayErrorMessage(fnfe.getMessage());

        LOGGER.error("Cannot find {}", lwjglNativesZipFile.toString(), fnfe);
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot extract {}", lwjglNativesZipFile.toString(), ioe);
      } finally {
        if (!lwjglNativesZipFile.delete()) {
          LOGGER.warn("Could not delete {}", lwjglNativesZipFile);
        }
      }
      return;
    }
  }

  public static void updateClasspath() {
    if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
      GameAppletWrapper.instance.setTaskState(EState.UPDATE_CLASSPATH.ordinal());
      GameAppletWrapper.instance.setTaskStateMessage(EState.UPDATE_CLASSPATH.getMessage());
      GameAppletWrapper.instance.setTaskProgressMessage(null);
      GameAppletWrapper.instance.setTaskProgress(90);
    }

    val jarFiles = binDirectoryPath.toFile().listFiles((dir, name) -> name.endsWith(".jar"));
    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    val clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
    val clientJarFile = versionDirectoryPath.resolve(clientJar).toFile();

    if (Objects.nonNull(jarFiles)) {
      val jarUrls = new URL[jarFiles.length + 1];
      try {
        for (int i = 0; i < jarFiles.length; i++) {
          jarUrls[i] = jarFiles[i].toURI().toURL();
        }

        jarUrls[jarFiles.length] = clientJarFile.toURI().toURL();
      } catch (MalformedURLException murle) {
        displayErrorMessage(murle.getMessage());

        LOGGER.error("Cannot convert {} to URL", jarUrls, murle);
      }

      GameAppletWrapper.instance.setMcAppletClassLoader(
          AccessController.doPrivileged(
              new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                  val contextClassLoader = Thread.currentThread().getContextClassLoader();
                  val urlClassLoader = new URLClassLoader(jarUrls, contextClassLoader);

                  Thread.currentThread().setContextClassLoader(urlClassLoader);
                  return urlClassLoader;
                }
              }));

      val libraryPaths = new String[] {"org.lwjgl.librarypath", "net.java.games.input.librarypath"};
      Arrays.stream(libraryPaths)
          .forEachOrdered(
              libraryPath -> System.setProperty(libraryPath, nativesDirectoryPath.toString()));
    }
  }

  private static int calcTotalExtractSize(File[] files) {
    int size = 0;

    for (val file : files) {
      try (val fis = new FileInputStream(file);
          val bis = new BufferedInputStream(fis);
          val zis = new ZipInputStream(bis)) {
        ZipEntry entry;
        while (Objects.nonNull(entry = zis.getNextEntry())) {
          size += entry.getSize();
        }
      } catch (FileNotFoundException fnfe) {
        displayErrorMessage(fnfe.getMessage());

        LOGGER.error("Cannot find {}", file.toString(), fnfe);
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot calculate size for {}", file.toString(), ioe);
      }
    }
    return size;
  }

  private static int calcTotalDownloadSize(URL[] urls) {
    int size = 0;

    val service = Executors.newFixedThreadPool(urls.length);
    val futures = new ArrayList<Future<Integer>>();
    for (val url : urls) {
      futures.add(
          service.submit(
              () -> {
                try {
                  val request = Request.head(url.toURI()).execute().returnResponse();
                  val contentLength = request.getFirstHeader(HttpHeaders.CONTENT_LENGTH);
                  return Integer.parseInt(contentLength.getValue());
                } catch (NumberFormatException nfe) {
                  displayErrorMessage(nfe.getMessage());

                  LOGGER.error("Cannot parse content size for {}", url.toString(), nfe);
                } catch (IOException ioe) {
                  displayErrorMessage(ioe.getMessage());

                  LOGGER.error("Cannot calculate content size for {}", url.toString(), ioe);
                } catch (URISyntaxException urise) {
                  displayErrorMessage(urise.getMessage());

                  LOGGER.error("Cannot parse {} as URI", url.toString(), urise);
                }
                return 0;
              }));
    }
    for (val future : futures) {
      try {
        size += future.get();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();

        LOGGER.error("Interrupted while calculating content size", ie);
      } catch (ExecutionException ee) {
        LOGGER.error("Error while calculating content size", ee);
      } finally {
        service.shutdown();
      }
    }
    return size;
  }

  private static void displayErrorMessage(String message) {
    GameAppletWrapper.instance.setUpdaterTaskErrored(true);

    val state = GameAppletWrapper.instance.getTaskState();
    val fatalErrorMessage =
        MessageFormat.format(
            LauncherLanguage.bundle.getString("gaw.taskStateMessage.error"), state, message);
    GameAppletWrapper.instance.setTaskStateMessage(fatalErrorMessage);
    GameAppletWrapper.instance.setTaskProgressMessage(null);
  }
}
