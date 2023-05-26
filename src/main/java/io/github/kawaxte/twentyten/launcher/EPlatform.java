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

package io.github.kawaxte.twentyten.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * This enum represents the various platforms and system architectures that the application
 * supports.
 *
 * <p>Each enum constant represents an operating system (OS) like LINUX, MACOS, and WINDOWS, or a
 * system architecture like AARCH64, AMD64, and X86. The application uses this enum to identify the
 * user's OS and system architecture for tasks such as creating OS-specific folders or downloading
 * architecture-specific files.
 *
 * <p>The OS constants have associated lists of names that can be found in the system property
 * "os.name", and the architecture constants have an associated name that can be found in the system
 * property "os.arch".
 *
 * <p>This enum also provides utility methods to determine the current OS and system architecture.
 *
 * @author Kawaxte
 * @since 1.4.2823_06
 */
public enum EPlatform {
  LINUX(Arrays.asList("aix", "nix", "nux"), null),
  MACOS(Arrays.asList("darwin", "mac"), null),
  WINDOWS(Collections.singletonList("win"), null),
  AARCH64(null, "aarch64"),
  AMD64(null, "amd64"),
  X86(null, "x86");

  public static final String OS_NAME;
  public static final String OS_ARCH;

  static {
    OS_NAME = System.getProperty("os.name");
    OS_ARCH = System.getProperty("os.arch");
  }

  private final List<String> names;
  private final String arch;

  EPlatform(List<String> names, String arch) {
    this.names =
        Objects.isNull(names) ? null : Collections.unmodifiableList(new ArrayList<>(names));
    this.arch = arch;
  }

  /**
   * Get the operating system name based on the system property "os.name".
   *
   * @return {@code null} if the OS name is not found in the system property "os.name".
   */
  public static EPlatform getOSName() {
    return Arrays.stream(values())
        .filter(
            platform ->
                Objects.nonNull(platform.names)
                    && platform.names.stream()
                        .anyMatch(name -> OS_NAME.toLowerCase(Locale.ROOT).contains(name)))
        .findFirst()
        .orElse(null);
  }

  /**
   * Get the system architecture based on the system property "os.arch".
   *
   * @return {@code null} if the system architecture is not found in the system property "os.arch".
   */
  public static EPlatform getOSArch() {
    return Arrays.stream(values())
        .filter(platform -> Objects.equals(platform.arch, OS_ARCH))
        .findFirst()
        .orElse(null);
  }

  public static boolean isWindows() {
    return Objects.equals(WINDOWS, getOSName());
  }

  public static boolean isMacOS() {
    return Objects.equals(MACOS, getOSName());
  }

  public static boolean isLinux() {
    return Objects.equals(LINUX, getOSName());
  }

  public static boolean isX86() {
    return Objects.equals(X86, getOSArch());
  }

  public static boolean isAMD64() {
    return Objects.equals(AMD64, getOSArch());
  }

  public static boolean isAARCH64() {
    return Objects.equals(AARCH64, getOSArch());
  }
}
