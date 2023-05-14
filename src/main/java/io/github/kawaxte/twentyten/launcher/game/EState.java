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
