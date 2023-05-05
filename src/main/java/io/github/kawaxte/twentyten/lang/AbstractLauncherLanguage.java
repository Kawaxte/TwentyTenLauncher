package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract class AbstractLauncherLanguage {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(AbstractLauncherLanguage.class);
  }

  @Getter UTF8ResourceBundle utf8Bundle;

  {
    this.utf8Bundle = new UTF8ResourceBundle();
  }

  public abstract void loadLanguage(String baseName, String isoCode);
}
