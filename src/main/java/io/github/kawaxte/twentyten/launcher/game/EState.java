package io.github.kawaxte.twentyten.launcher.game;

import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import lombok.Getter;

public enum EState {
  INITIALISE(LauncherLanguage.bundle.getString("es_enum.initialise")),
  CHECK_CACHE(LauncherLanguage.bundle.getString("es_enum.checkCache")),
  DOWNLOAD_PACKAGES(LauncherLanguage.bundle.getString("es_enum.downloadPackages")),
  EXTRACT_PACKAGES(LauncherLanguage.bundle.getString("es_enum.extractPackages")),
  UPDATE_CLASSPATH(LauncherLanguage.bundle.getString("es_enum.updateClasspath")),
  DONE(LauncherLanguage.bundle.getString("es_enum.done"));

  @Getter private final String stateMessage;

  EState(String stateMessage) {
    this.stateMessage = stateMessage;
  }
}
