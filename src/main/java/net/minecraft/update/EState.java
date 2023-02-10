package net.minecraft.update;

import ee.twentyten.util.LanguageHelper;
import lombok.Getter;
import lombok.Setter;

public enum EState {
  INIT_STATE(LanguageHelper.getString("es.string.initState.text")),
  CACHE_STATE(LanguageHelper.getString("es.string.cacheState.text")),
  DETERMINE_STATE(LanguageHelper.getString("es.string.determineState.text")),
  RETRIEVE_STATE(LanguageHelper.getString("es.string.retrieveState.text")),
  DOWNLOAD_STATE(LanguageHelper.getString("es.string.downloadState.text")),
  MOVE_STATE(LanguageHelper.getString("es.string.moveState.text")),
  CLASSPATH_STATE(LanguageHelper.getString("es.string.classpathState.text")),
  DONE_STATE(LanguageHelper.getString("es.string.doneState.text"));

  @Getter
  @Setter
  private static EState state;
  @Getter
  private final String message;

  EState(String message) {
    this.message = message;
  }
}
