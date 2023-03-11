package ee.twentyten.minecraft.update;

import ee.twentyten.util.launcher.options.LanguageUtils;
import lombok.Getter;
import lombok.Setter;

public enum EState {
  INIT(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.initState")),
  CHECK_CACHE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.checkCacheState")),
  DETERMINE_PACKAGE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.determinePackageState")),
  DOWNLOAD_PACKAGE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.downloadPackageState")),
  EXTRACT_PACKAGE(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.extractPackageState")),
  UPDATE_CLASSPATH(LanguageUtils
      .getString(LanguageUtils.getBundle(), "es.state.updateClasspathState")),
  DONE(LanguageUtils
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
