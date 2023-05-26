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

package io.github.kawaxte.twentyten.launcher.game;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import lombok.Getter;

/**
 * This enum represents the various states the {@link
 * io.github.kawaxte.twentyten.launcher.game.GameUpdater} can be in during its execution.
 *
 * <p>Each constant of this enum corresponds to a specific operation in the update process, and is
 * associated with a message that describes this operation. These messages are internationalised
 * strings stored in resource bundles, and are retrieved based on the current launcher language.
 *
 * <p>The state constants and their corresponding operations are as follows:
 *
 * <ol>
 *   <li>INITIALISE: The update process is being initialised.
 *   <li>CHECK_CACHE: The cache for existing files is being checked.
 *   <li>DOWNLOAD_PACKAGES: Packages necessary for the update are being downloaded.
 *   <li>EXTRACT_PACKAGES: The downloaded packages are being extracted.
 *   <li>UPDATE_CLASSPATH: The classpath is being updated.
 *   <li>DONE: The update process is complete.
 * </ol>
 *
 * <p>These states are used by the {@link io.github.kawaxte.twentyten.launcher.game.GameUpdater} to
 * determine and display the current operation being performed.
 *
 * @author Kawaxte
 * @since 1.5.1023_04
 */
public enum EState {
  INITIALISE(LauncherLanguageUtils.getESEnumKeys()[0]),
  CHECK_CACHE(LauncherLanguageUtils.getESEnumKeys()[1]),
  DOWNLOAD_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[2]),
  EXTRACT_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[3]),
  UPDATE_CLASSPATH(LauncherLanguageUtils.getESEnumKeys()[4]),
  DONE(LauncherLanguageUtils.getESEnumKeys()[5]);

  @Getter private final String message;

  /**
   * Constructor for each enum constant. It takes a key which is used to retrieve the corresponding
   * message from the resource bundle based on the current launcher language.
   *
   * @param key The key used to retrieve the corresponding message from the resource bundle.
   * @see LauncherLanguageUtils#getESEnumKeys()
   */
  EState(String key) {
    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.message = bundle.getString(key);
  }
}
