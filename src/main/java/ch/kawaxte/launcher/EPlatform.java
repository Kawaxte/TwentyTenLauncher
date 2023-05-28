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

package ch.kawaxte.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Enum representing the supported operating systems and architectures in the application.
 *
 * <p>The constants in this Enum correspond to operating systems (Linux, macOS, Windows) and
 * processor architectures (x86, x86_64, ARM64). {@code names} is a list of identifying strings for
 * each OS, and {@code arch} is an identifying string for each architecture.
 *
 * <p>The static variables {@code OS_NAME} and {@code OS_ARCH} capture the system properties for the
 * running OS and architecture respectively.
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
   * This method goes through the Enum values and finds the one whose names list contains a
   * substring of the system's OS name.
   *
   * @return The matching {@link EPlatform} for the operating system, or {@code null} if no match is
   *     found
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
   * This method goes through the Enum values and finds the one whose {@code arch} string matches
   * the system's architecture name.
   *
   * @return The matching {@link EPlatform} for the system architecture, or {@code null} if no match
   *     is found
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
