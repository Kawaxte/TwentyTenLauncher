package ch.kawaxte.launcher.util;

import ch.kawaxte.launcher.LauncherConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftUtils {

  private static final Logger LOGGER;
  private static final Path LOGS_DIRECTORY_PATH;

  static {
    LOGGER = LoggerFactory.getLogger(MinecraftUtils.class);

    LOGS_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("logs");
  }

  private MinecraftUtils() {}

  private static Path getFilePath(String username) {
    String selectedVersion = (String) LauncherConfig.get(4);
    String now = LocalDateTime.now(ZoneId.systemDefault()).toString();
    String formattedNow = now.replaceAll("\\D", "").substring(0, 8);
    String fileName = String.format("%s_%s.log", selectedVersion, username);
    String timestampedFileName =
        String.format("%s_%s_%d.log", selectedVersion, username, System.currentTimeMillis());

    Path filePath = LOGS_DIRECTORY_PATH.resolve(fileName);
    File file = filePath.toFile();
    if (file.exists() && file.length() > 0) {
      Path timestampedFilePath = LOGS_DIRECTORY_PATH.resolve(timestampedFileName);
      if (!file.renameTo(timestampedFilePath.toFile())) {
        LOGGER.error("Cannot rename log to '{}'", timestampedFilePath);
        return filePath;
      }

      archiveLogs(selectedVersion, formattedNow);
    }
    return filePath;
  }

  private static void archiveLogs(String versionId, String now) {
    String logFilePattern = String.format("%s_.*\\.log", versionId);

    Path zipPath = LOGS_DIRECTORY_PATH.resolve(String.format("%s_%s.zip", versionId, now));
    try (ZipOutputStream zos =
        new ZipOutputStream(
            Files.newOutputStream(zipPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
      List<Path> filePath;
      try (Stream<Path> logs =
          Files.list(LOGS_DIRECTORY_PATH)
              .filter(
                  path -> {
                    String fileName = path.getFileName().toString();
                    String zipFileName = zipPath.getFileName().toString();
                    return !Objects.equals(fileName, zipFileName)
                        && Pattern.matches(logFilePattern, fileName);
                  })) {
        filePath = logs.collect(Collectors.toList());
      }

      for (Path p : filePath) {
        ZipEntry zipEntry = new ZipEntry(p.getFileName().toString());
        zos.putNextEntry(zipEntry);
        Files.copy(p, zos);

        zos.closeEntry();
        Files.delete(p);
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot archive logs", ioe);
    }
  }

  public static void reassignOutputStream(String username) {
    Path filePath = getFilePath(username);
    try {
      Files.createDirectories(LOGS_DIRECTORY_PATH);
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot create {}", filePath, ioe);
    }

    try {
      PrintStream ps =
          new PrintStream(filePath.toFile()) {
            @Override
            public void println(String x) {
              String filter = addToFilter(x);
              super.println(filter);
            }

            private String addToFilter(String x) {
              String settingUser = "Setting user: ";
              if (x.startsWith(settingUser)) {
                String[] split = x.split(", ");
                if (split.length == 2) {
                  return new StringBuilder().append(split[0]).append(", <sessionId>").toString();
                }
              }
              return x;
            }
          };
      System.setOut(ps);
      System.setErr(ps);
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Cannot create {}", filePath, fnfe);
    }
  }
}
