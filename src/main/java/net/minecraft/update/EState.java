package net.minecraft.update;

import ee.twentyten.util.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

public enum EState {
  INIT_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.initState")),
  CACHE_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.cacheState")),
  DETERMINE_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.determineState")),
  RETRIEVE_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.retrieveState")),
  DOWNLOAD_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.downloadState")),
  EXTRACT_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.extractState")),
  UPDATE_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.updateState")),
  DONE_STATE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.doneState"));

  @Getter
  @Setter
  private static EState instance;
  @Getter
  private final String message;

  EState(String message) {
    this.message = message;
  }
}
