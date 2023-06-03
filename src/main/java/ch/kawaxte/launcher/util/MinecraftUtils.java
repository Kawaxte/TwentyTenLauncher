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

package ch.kawaxte.launcher.util;

import ch.kawaxte.launcher.LauncherConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling everything related to Minecraft.
 *
 * @author Kawaxte
 * @since 1.5.2823_06
 */
public final class MinecraftUtils {

  private static final Logger LOGGER;
  private static final Path LOGS_DIRECTORY_PATH;

  static {
    LOGGER = LoggerFactory.getLogger(MinecraftUtils.class);

    LOGS_DIRECTORY_PATH = LauncherUtils.WORKING_DIRECTORY_PATH.resolve("logs");
  }

  private MinecraftUtils() {}

  /**
   * Returns the path to the log file for the specified username.
   *
   * @return {@code null} if the file cannot be created, otherwise the path to the log
   */
  private static Path getFilePath(String username) {
    String selectedVersion = (String) LauncherConfig.get(4);
    selectedVersion = selectedVersion.replaceAll("[._]", "");

    long currentTime = System.currentTimeMillis();

    String fileName = String.format("%s_%s_%s.log", selectedVersion, username, currentTime);

    Path filePath = LOGS_DIRECTORY_PATH.resolve(fileName);
    try {
      Files.createDirectories(LOGS_DIRECTORY_PATH);
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
      return filePath;
    } catch (IOException ioe) {
      LOGGER.error("Cannot create {}", filePath, ioe);
    }
    return null;
  }

  /**
   * Reassigns the {@link System#out} and {@link System#err} streams to a file in the {@code logs}
   * directory.
   *
   * <p>Additonally, it filters out the {@code sessionid} parameter from the {@code Setting user: }
   * line that is printed to the console by one of the classes within the client executable.
   *
   * @param username the {@code username} parameter from MinecraftApplet
   */
  public static void reassignOutputStream(String username) {
    Path filePath = getFilePath(username);
    if (Objects.isNull(filePath)) {
      return;
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
                String[] split = x.split(",");
                String[] tokenSplit = split[1].split(":");
                if (tokenSplit.length == 3) {
                  String accessToken = tokenSplit[1];
                  String profileId = tokenSplit[2];
                  return new StringBuilder()
                      .append(split[0])
                      .append(", token:")
                      .append(accessToken, 0, 0)
                      .append("<accessToken>:")
                      .append(profileId)
                      .toString();
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
