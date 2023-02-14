package net.minecraft.update;

import ee.twentyten.lang.LauncherLanguage;
import lombok.Getter;
import lombok.Setter;

public enum EState {
  INIT_STATE(LauncherLanguage
      .getString("es.state.initState")),
  CACHE_STATE(LauncherLanguage
      .getString("es.state.cacheState")),
  DETERMINE_STATE(LauncherLanguage
      .getString("es.state.determineState")),
  RETRIEVE_STATE(LauncherLanguage
      .getString("es.state.retrieveState")),
  DOWNLOAD_STATE(LauncherLanguage
      .getString("es.state.downloadState")),
  MOVE_STATE(LauncherLanguage
      .getString("es.state.moveState")),
  CLASSPATH_STATE(LauncherLanguage
      .getString("es.state.classpathState")),
  DONE_STATE(LauncherLanguage
      .getString("es.state.doneState"));

  @Getter
  @Setter
  private static EState state;
  @Getter
  private final String message;

  EState(String message) {
    this.message = message;
  }
}
