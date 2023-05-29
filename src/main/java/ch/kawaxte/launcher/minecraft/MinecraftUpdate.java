/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.kawaxte.launcher.minecraft;

import ch.kawaxte.launcher.EPlatform;
import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.ui.MinecraftAppletWrapper;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for updating Minecraft.
 *
 * <p>It checks if the necessary files exist, downloads any missing files, and updates the
 * classpath. If the files are already cached, it will not download them again.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.1223_05
 */
public final class MinecraftUpdate {

  private static final Logger LOGGER;
  private static final Path BIN_DIRECTORY_PATH;
  private static final Path NATIVES_DIRECTORY_PATH;
  private static final Path VERSION_DIRECTORY_PATH;
  private static final Path VERSIONS_DIRECTORY_PATH;

  static {
    LOGGER = LoggerFactory.getLogger(MinecraftUpdate.class);

    BIN_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("bin");
    NATIVES_DIRECTORY_PATH = BIN_DIRECTORY_PATH.resolve("natives");

    File nativesDirectory = NATIVES_DIRECTORY_PATH.toFile();
    if (!nativesDirectory.exists() && !nativesDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", NATIVES_DIRECTORY_PATH);
    }

    String selectedVersion = (String) LauncherConfig.get(4);
    VERSIONS_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("versions");
    VERSION_DIRECTORY_PATH = VERSIONS_DIRECTORY_PATH.resolve(selectedVersion);

    File versionDirectory = VERSION_DIRECTORY_PATH.toFile();
    if (!versionDirectory.exists() && !versionDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", VERSION_DIRECTORY_PATH);
    }
  }

  private MinecraftUpdate() {}

  /**
   * Checks if the minecraft is cached by verifying the existence of necessary files.
   *
   * @return {@code true} if all the necessary files exist, {@code false} otherwise
   */
  public static boolean isGameCached() {
    return isLWJGLJarExistent() && isLWJGLNativeExistent() && isClientJarExistent();
  }

  /**
   * Checks if all the necessary LWJGL2 .JAR files exist in the ./bin/ directory.
   *
   * <p>The necessary LWJGL2 .JAR files are:
   *
   * <ul>
   *   <li>lwjgl.jar
   *   <li>lwjgl_util.jar
   *   <li>jinput.jar
   *   <li>jutils.jar
   * </ul>
   *
   * @return {@code true} if all the necessary LWJGL2 .JAR files exist, {@code false} otherwise
   */
  private static boolean isLWJGLJarExistent() {
    List<String> lwjglJars = getListOfLWJGLJars();
    return lwjglJars.stream()
        .allMatch(lwjglJar -> Files.exists(BIN_DIRECTORY_PATH.resolve(lwjglJar)));
  }

  /**
   * Checks if the necessary LWJGL2 native files exist in the ./bin/natives/ directory.
   *
   * <p>The necessary LWJGL2 native files depend on platform:
   *
   * <ul>
   *   <li>Linux (32-bit): liblwjgl.so, libopenal.so, libopenal.so
   *   <li>Linux: liblwjgl64.so, liblwjgl64.so, libopenal64.so
   *   <li>macOS: liblwjgl.jnilib, libjinput-osx.jnilib, openal.dylib
   *   <li>Windows (32-bit): lwjgl.dll, jinput-dx8.dll, jinput-raw.dll, jintput-wintab.dll,
   *       OpenAL32.dll
   *   <li>Windows: lwjgl64.dll, jinput-dx8_64.dll, jinput-raw_64.dll, jintput-wintab.dll,
   *       OpenAL64.dll
   * </ul>
   *
   * @return {@code true} if the necessary LWJGL2 native files exist, {@code false} otherwise
   */
  private static boolean isLWJGLNativeExistent() {
    List<String> listOfLWJGLNatives = getListOfLWJGLNatives();
    return listOfLWJGLNatives.stream()
        .findFirst()
        .filter(lwjglNative -> Files.exists(NATIVES_DIRECTORY_PATH.resolve(lwjglNative)))
        .isPresent();
  }

  /**
   * Checks if the Minecraft client .JAR file exists in the ./versions/%version%/ directory.
   *
   * @return {@code true} if the client .JAR file exists, {@code false} otherwise
   */
  private static boolean isClientJarExistent() {
    String selectedVersion = (String) LauncherConfig.get(4);
    String clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
    return Files.exists(VERSION_DIRECTORY_PATH.resolve(clientJar));
  }

  /**
   * Fetches the required URLs for LWJGL2.
   *
   * @return an ArrayList of required URLs for LWJGL2
   */
  private static List<String> getListOfLWJGLJars() {
    return Arrays.asList("jinput.jar", "jutils.jar", "lwjgl.jar", "lwjgl_util.jar");
  }

  /**
   * Fetches the required native files for LWJGL2 based on platform and architecture.
   *
   * @return an ArrayList of required native files for LWJGL2
   */
  private static List<String> getListOfLWJGLNatives() {
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
    return new ArrayList<>();
  }

  /**
   * Fetches the required URLs for the Minecraft client based on the available versions.
   *
   * @return an array of required URLs for the Minecraft client
   */
  private static GenericUrl[] getClientUrls() {
    GenericUrl[] urls = new GenericUrl[3];
    urls[0] =
        new GenericUrl(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("twentyten-launcher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("client/")
                .append("legacy_beta/")
                .toString());
    urls[1] =
        new GenericUrl(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("twentyten-launcher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("client/")
                .append("legacy_alpha/")
                .toString());
    urls[2] =
        new GenericUrl(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("twentyten-launcher/")
                .append("raw/")
                .append("nightly/")
                .append("bin/")
                .append("client/")
                .append("legacy_infdev/")
                .toString());
    return urls;
  }

  /**
   * Fetches the required URLs for LWJGL2 files based on platform and architecture.
   *
   * @return a URL for the LWJGL2 files
   */
  private static GenericUrl getLWJGLUrls() {
    if (EPlatform.isAARCH64()) {
      return new GenericUrl(
          new StringBuilder()
              .append("https://github.com/")
              .append("Kawaxte/")
              .append("twentyten-launcher/")
              .append("raw/")
              .append("nightly/")
              .append("bin/")
              .append("lwjgl/")
              .append("aarch64/")
              .toString());
    }
    if (EPlatform.isAMD64()) {
      return new GenericUrl(
          new StringBuilder()
              .append("https://github.com/")
              .append("Kawaxte/")
              .append("twentyten-launcher/")
              .append("raw/")
              .append("nightly/")
              .append("bin/")
              .append("lwjgl/")
              .append("amd64/")
              .toString());
    }
    if (EPlatform.isX86()) {
      return new GenericUrl(
          new StringBuilder()
              .append("https://github.com/")
              .append("Kawaxte/")
              .append("twentyten-launcher/")
              .append("raw/")
              .append("nightly/")
              .append("bin/")
              .append("lwjgl/")
              .append("x86/")
              .toString());
    }
    return new GenericUrl("");
  }

  /**
   * Fetches the required URLs for all the required files.
   *
   * @return an array of required URLs for all the required files for Minecraft to run
   */
  public static GenericUrl[] getGenericUrls() {
    String selectedVersion = (String) LauncherConfig.get(4);
    String clientJar = String.format("%s.jar", selectedVersion);
    String[] lwjglNativesZips = {"natives-linux.zip", "natives-macosx.zip", "natives-windows.zip"};

    GenericUrl[] clientUrls = getClientUrls();
    GenericUrl lwjglUrls = getLWJGLUrls();
    GenericUrl[] urls = new GenericUrl[6];
    GenericUrl clientUrl = getClientUrlByPrefix(selectedVersion, clientUrls);

    try {
      if (!isLWJGLJarExistent()) {
        for (int i = 0; i < getListOfLWJGLJars().size(); i++) {
          urls[i] = new GenericUrl(new URL(lwjglUrls.toURL(), getListOfLWJGLJars().get(i)));
        }
      }
      if (!isLWJGLNativeExistent()) {
        String lwjglNativesZip = getLwjglNativeZipByPlatform(lwjglNativesZips);

        if (Objects.nonNull(lwjglNativesZip)) {
          urls[4] = new GenericUrl(new URL(lwjglUrls.toURL(), lwjglNativesZip));
        }
      }
      if (!isClientJarExistent() && (Objects.nonNull(clientUrl))) {
        urls[5] = new GenericUrl(new URL(clientUrl.toURL(), clientJar));
      }
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create generic URL(s)", murle);
    }
    return Arrays.stream(urls).filter(Objects::nonNull).toArray(GenericUrl[]::new);
  }

  /**
   * Parses the version ID and returns the correct URL for the client.
   *
   * @param versionId the `selectedVersion` value from the config
   * @param urls the array of URLs
   * @return the correct URL for the client
   */
  private static GenericUrl getClientUrlByPrefix(String versionId, GenericUrl[] urls) {
    if (versionId.startsWith("b")) {
      return urls[0];
    }
    if (versionId.startsWith("a")) {
      return urls[1];
    }
    if (versionId.startsWith("i")) {
      return urls[2];
    }
    return new GenericUrl("");
  }

  /**
   * Returns the correct LWJGL native ZIP file based on the platform.
   *
   * @param zips the array of ZIP files
   * @return the correct LWJGL native ZIP file
   */
  private static String getLwjglNativeZipByPlatform(String[] zips) {
    if (EPlatform.isLinux()) {
      return zips[0];
    }
    if (EPlatform.isMacOS()) {
      return zips[1];
    }
    if (EPlatform.isWindows()) {
      return zips[2];
    }
    return null;
  }

  /**
   * Downloads the required files after checking if they exist.
   *
   * <p>It first downloads the files to the temporary directory and then moves them to their
   * respective locations in the working directory once all the files have been downloaded.
   *
   * <p>Additionally, the total download size is calculated and the progress is updated accordingly.
   *
   * @param urls the required URLs for all the required files for Minecraft to run
   * @see #download(Path, AtomicInteger, int, GenericUrl)
   * @see #calculateTotalDownloadSize(GenericUrl[])
   */
  public static void downloadPackages(GenericUrl[] urls) {
    if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
      MinecraftAppletWrapper.getInstance().setTaskState(EState.DOWNLOAD_PACKAGES.ordinal());
      MinecraftAppletWrapper.getInstance()
          .setTaskStateMessage(EState.DOWNLOAD_PACKAGES.getMessage());
      MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
      MinecraftAppletWrapper.getInstance().setTaskProgress(10);
    }

    String javaIoTmpdir = System.getProperty("java.io.tmpdir");
    Path javaIoTmpDirPath = Paths.get(javaIoTmpdir);

    AtomicInteger currentDownloadSize = new AtomicInteger(0);
    int totalDownloadSize = calculateTotalDownloadSize(urls);
    if (totalDownloadSize == 0) {
      return;
    }

    Arrays.stream(urls)
        .forEachOrdered(
            url -> download(javaIoTmpDirPath, currentDownloadSize, totalDownloadSize, url));
    move(javaIoTmpDirPath);
  }

  /**
   * Calculates the total download size of all the required files.
   *
   * @param urls the required URLs for all the required files for Minecraft to run
   * @return the total download size of all the required files
   */
  private static int calculateTotalDownloadSize(GenericUrl[] urls) {
    HttpTransport transport = new NetHttpTransport();

    HttpRequestFactory factory = transport.createRequestFactory();

    long size = 0L;
    for (GenericUrl url : urls) {
      try {
        HttpRequest request = factory.buildHeadRequest(url);
        HttpResponse response = request.execute();

        HttpHeaders headers = response.getHeaders();
        long contentLength = headers.getContentLength();
        size += contentLength;
      } catch (NumberFormatException nfe) {
        displayErrorMessage(nfe.getMessage());

        LOGGER.error("Cannot parse size for {}", url, nfe);
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot calculate size for {}", url, ioe);
      }
    }
    return (int) size;
  }

  /**
   * Performs the download of the required files.
   *
   * @param p the path to the temporary directory
   * @param current the current download size
   * @param total the total download size
   * @param url the URL of the required file
   */
  private static void download(Path p, AtomicInteger current, int total, GenericUrl url) {
    HttpTransport transport = new NetHttpTransport();

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildGetRequest(url);
      HttpResponse response = request.execute();

      int fileNameIndex = url.toString().lastIndexOf("/") + 1;
      String fileName = url.toString().substring(fileNameIndex);
      File file = p.resolve(fileName).toFile();

      try (BufferedInputStream bis = new BufferedInputStream(response.getContent());
          FileOutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[65536];
        int read;

        while ((read = bis.read(buffer)) != -1) {
          fos.write(buffer, 0, read);
          current.addAndGet(read);

          int downloadProgress = (current.get() * 100) / total;
          int progress = 10 + ((current.get() * 45) / total);
          MinecraftAppletWrapper.getInstance()
              .setTaskProgressMessage(
                  LauncherLanguageUtils.getGAWKeys()[3], fileName, downloadProgress);
          MinecraftAppletWrapper.getInstance().setTaskProgress(progress);
        }
      }
    } catch (FileNotFoundException fnfe) {
      displayErrorMessage(fnfe.getMessage());

      LOGGER.error("Cannot find {}", url, fnfe);
    } catch (IOException ioe) {
      displayErrorMessage(ioe.getMessage());

      LOGGER.error("Cannot download {}", url, ioe);
    }
  }

  /**
   * Moves the downloaded files to their respective locations in the working directory.
   *
   * @param p the path to the temporary directory
   */
  private static void move(Path p) {
    String selectedVersion = (String) LauncherConfig.get(4);
    String clientJar = String.format("%s.jar", selectedVersion);

    Consumer<String> move =
        fileName -> {
          File file = p.resolve(fileName).toFile();
          File fileDest;

          if (Objects.equals(fileName, clientJar)) {
            fileDest = VERSION_DIRECTORY_PATH.resolve(fileName).toFile();
          } else
            fileDest =
                fileName.startsWith("natives-") && fileName.endsWith(".zip")
                    ? NATIVES_DIRECTORY_PATH.resolve(fileName).toFile()
                    : BIN_DIRECTORY_PATH.resolve(fileName).toFile();

          try {
            if (file.exists()) {
              Files.move(file.toPath(), fileDest.toPath());
            }
          } catch (IOException ioe) {
            displayErrorMessage(ioe.getMessage());

            LOGGER.error("Cannot move {} to {}", file, fileDest, ioe);
          }
        };
    move.accept(clientJar);

    File[] zipFiles =
        p.toFile().listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
    if (Objects.nonNull(zipFiles)) {
      Arrays.stream(zipFiles).map(File::getName).forEach(move);
    }

    List<String> jarFiles = getListOfLWJGLJars();
    jarFiles.forEach(move);
  }

  /**
   * Extracts the downloaded native package.
   *
   * @see #extract(AtomicInteger, int, File)
   * @see #calculateTotalExtractSize(File[])
   */
  public static void extractDownloadedPackages() {
    if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
      MinecraftAppletWrapper.getInstance().setTaskState(EState.EXTRACT_PACKAGES.ordinal());
      MinecraftAppletWrapper.getInstance()
          .setTaskStateMessage(EState.EXTRACT_PACKAGES.getMessage());
      MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
      MinecraftAppletWrapper.getInstance().setTaskProgress(55);
    }

    File[] zipFiles =
        NATIVES_DIRECTORY_PATH
            .toFile()
            .listFiles((dir, name) -> name.startsWith("natives-") && name.endsWith(".zip"));
    if (Objects.isNull(zipFiles)) {
      return;
    }

    AtomicInteger currentExtractSize = new AtomicInteger(0);
    int totalExtractSize = calculateTotalExtractSize(zipFiles);
    if (totalExtractSize == 0) {
      return;
    }

    Arrays.stream(zipFiles).forEachOrdered(f -> extract(currentExtractSize, totalExtractSize, f));
  }

  /**
   * Calculates the total size of files to be extracted.
   *
   * @param files the files to calculate the size of
   * @return the total size of the files to be extracted
   */
  private static int calculateTotalExtractSize(File[] files) {
    long size = 0L;

    for (File file : files) {
      try (FileInputStream fis = new FileInputStream(file);
          BufferedInputStream bis = new BufferedInputStream(fis);
          ZipInputStream zis = new ZipInputStream(bis)) {
        ZipEntry entry;

        while (Objects.nonNull(entry = zis.getNextEntry())) {
          size += entry.getSize();
        }
      } catch (FileNotFoundException fnfe) {
        displayErrorMessage(fnfe.getMessage());

        LOGGER.error("Cannot find {}", file, fnfe);
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot calculate size for {}", file, ioe);
      }
    }
    return (int) size;
  }

  /**
   * Performs the extraction of the native package.
   *
   * @param current the current size of the extracted files
   * @param total the total size of the files to be extracted
   * @param file the file to extract
   */
  private static void extract(AtomicInteger current, int total, File file) {
    try (FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ZipInputStream zis = new ZipInputStream(bis)) {
      ZipEntry entry;

      while (Objects.nonNull(entry = zis.getNextEntry())) {
        String name = entry.getName();
        if (!entry.isDirectory() && name.indexOf(47) != -1) {
          continue;
        }

        File nativeFile = NATIVES_DIRECTORY_PATH.resolve(name).toFile();
        String nativeFileCanonicalPath = nativeFile.getCanonicalPath();
        if (!nativeFileCanonicalPath.startsWith(NATIVES_DIRECTORY_PATH.toString())) {
          LOGGER.error("{} is not a valid path", nativeFile);
          return;
        }

        try (FileOutputStream fos = new FileOutputStream(nativeFile)) {
          byte[] buffer = new byte[65536];
          int read;

          while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer, 0, read);
            current.addAndGet(read);

            int extractProgress = (current.get() * 100) / total;
            int progress = 55 + ((current.get() * 30) / total);
            MinecraftAppletWrapper.getInstance()
                .setTaskProgressMessage(
                    LauncherLanguageUtils.getGAWKeys()[3], name, extractProgress);
            MinecraftAppletWrapper.getInstance().setTaskProgress(progress);
          }
        }
      }
    } catch (FileNotFoundException fnfe) {
      displayErrorMessage(fnfe.getMessage());

      LOGGER.error("Cannot find {}", file, fnfe);
    } catch (IOException ioe) {
      displayErrorMessage(ioe.getMessage());

      LOGGER.error("Cannot extract {}", file, ioe);
    } finally {
      try {
        if (!Files.deleteIfExists(file.toPath())) {
          LOGGER.warn("Could not delete {}", file);
        }
      } catch (IOException ioe) {
        displayErrorMessage(ioe.getMessage());

        LOGGER.error("Cannot delete {}", file, ioe);
      }
    }
  }

  /**
   * Updates the classpath to take into account the downloaded jars, then loads the native libraries
   * to their respective JVM arguments.
   */
  public static void updateClasspath() {
    if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
      MinecraftAppletWrapper.getInstance().setTaskState(EState.UPDATE_CLASSPATH.ordinal());
      MinecraftAppletWrapper.getInstance()
          .setTaskStateMessage(EState.UPDATE_CLASSPATH.getMessage());
      MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
      MinecraftAppletWrapper.getInstance().setTaskProgress(90);
    }

    File[] jarFiles = BIN_DIRECTORY_PATH.toFile().listFiles((dir, name) -> name.endsWith(".jar"));
    String selectedVersion = (String) LauncherConfig.get(4);
    String clientJar = new StringBuilder().append(selectedVersion).append(".jar").toString();
    File clientJarFile = VERSION_DIRECTORY_PATH.resolve(clientJar).toFile();

    if (Objects.nonNull(jarFiles)) {
      URL[] jarUrls = new URL[jarFiles.length + 1];
      try {
        for (int i = 0; i < jarFiles.length; i++) {
          jarUrls[i] = jarFiles[i].toURI().toURL();
        }

        jarUrls[jarFiles.length] = clientJarFile.toURI().toURL();
      } catch (MalformedURLException murle) {
        displayErrorMessage(murle.getMessage());

        LOGGER.error("Cannot parse {} as URL", jarUrls, murle);
      }

      MinecraftAppletWrapper.getInstance()
          .setMcAppletClassLoader(
              AccessController.doPrivileged(
                  (PrivilegedAction<URLClassLoader>)
                      () -> {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        URLClassLoader urlLoader = new URLClassLoader(jarUrls, loader);

                        Thread.currentThread().setContextClassLoader(urlLoader);
                        return urlLoader;
                      }));

      String[] libraryPaths =
          new String[] {"org.lwjgl.librarypath", "net.java.games.input.librarypath"};
      Arrays.stream(libraryPaths)
          .forEachOrdered(
              libraryPath -> System.setProperty(libraryPath, NATIVES_DIRECTORY_PATH.toString()));
    }
  }

  /**
   * Displays an error message if an error occurs during the update process.
   *
   * @param message the error message to display
   */
  private static void displayErrorMessage(String message) {
    MinecraftAppletWrapper.getInstance().setUpdaterTaskErrored(true);

    UTF8ResourceBundle bundle = LauncherLanguage.getBundle();

    int state = MinecraftAppletWrapper.getInstance().getTaskState();
    String fatalErrorMessage =
        MessageFormat.format(
            bundle.getString(LauncherLanguageUtils.getGAWKeys()[2]), state, message);
    MinecraftAppletWrapper.getInstance().setTaskStateMessage(fatalErrorMessage);
    MinecraftAppletWrapper.getInstance().setTaskProgressMessage(null);
  }
}
