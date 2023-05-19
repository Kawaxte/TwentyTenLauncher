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

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public enum EPlatform {
  LINUX(new String[] {"aix", "nix", "nux"}),
  MACOS(new String[] {"darwin", "mac"}),
  WINDOWS(new String[] {"win"}),
  AARCH64("aarch64"),
  AMD64("amd64"),
  X86("x86");

  public static final String OS_NAME;
  public static final String OS_ARCH;

  static {
    OS_NAME = System.getProperty("os.name");
    OS_ARCH = System.getProperty("os.arch");
  }

  private String[] names;
  private String arch;

  EPlatform(String[] names) {
    this.names = names;
  }

  EPlatform(String arch) {
    this.arch = arch;
  }

  public static EPlatform getOSName() {
    return Arrays.stream(values())
        .filter(
            platform ->
                Arrays.stream(platform.names)
                    .anyMatch(name -> OS_NAME.toLowerCase(Locale.ROOT).contains(name)))
        .findFirst()
        .orElse(null);
  }

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
