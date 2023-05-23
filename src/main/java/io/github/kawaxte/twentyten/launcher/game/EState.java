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

public enum EState {
  INITIALISE(LauncherLanguageUtils.getESEnumKeys()[0]),
  CHECK_CACHE(LauncherLanguageUtils.getESEnumKeys()[1]),
  DOWNLOAD_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[2]),
  EXTRACT_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[3]),
  UPDATE_CLASSPATH(LauncherLanguageUtils.getESEnumKeys()[4]),
  DONE(LauncherLanguageUtils.getESEnumKeys()[5]);

  @Getter private final String message;

  EState(String message) {
    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.message = bundle.getString(message);
  }
}
