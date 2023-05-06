package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract class AbstractLauncherLanguage {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(AbstractLauncherLanguage.class);
  }

  @Getter UTF8ResourceBundle bundle;

  {
    this.bundle = new UTF8ResourceBundle();
  }

  public abstract void load(String baseName, String isoCode);
}
