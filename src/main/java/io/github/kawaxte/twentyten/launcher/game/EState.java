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

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import lombok.Getter;
import lombok.val;

public enum EState {
  INITIALISE("es_enum.initialise"),
  CHECK_CACHE("es_enum.checkCache"),
  DOWNLOAD_PACKAGES("es_enum.downloadPackages"),
  EXTRACT_PACKAGES("es_enum.extractPackages"),
  UPDATE_CLASSPATH("es_enum.updateClasspath"),
  DONE("es_enum.done");

  @Getter private final String message;

  EState(String message) {
    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.message = bundle.getString(message);
  }
}
