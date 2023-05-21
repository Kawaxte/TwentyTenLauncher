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

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ELookAndFeel {
  GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
  AQUA("com.apple.laf.AquaLookAndFeel"),
  WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(ELookAndFeel.class);
  }

  @Getter private final String className;

  ELookAndFeel(String className) {
    this.className = className;
  }

  public static void setLookAndFeel() {
    try {
      if (EPlatform.isLinux()) {
        UIManager.setLookAndFeel(GTK.getClassName());
      }
      if (EPlatform.isMacOS()) {
        UIManager.setLookAndFeel(AQUA.getClassName());
      }
      if (EPlatform.isWindows()) {
        UIManager.setLookAndFeel(WINDOWS.getClassName());
      }
    } catch (ClassNotFoundException cnfe) {
      LOGGER.error(
          "Cannot find '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          cnfe);
    } catch (InstantiationException ie) {
      LOGGER.error(
          "Cannot instantiate '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          ie);
    } catch (IllegalAccessException iae) {
      LOGGER.error(
          "Cannot access '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          iae);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LOGGER.error(
          "'{}' unsupported on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          ulafe);
    } finally {
      LOGGER.info("Set look and feel to '{}'", UIManager.getLookAndFeel().getName());
    }
  }
}
