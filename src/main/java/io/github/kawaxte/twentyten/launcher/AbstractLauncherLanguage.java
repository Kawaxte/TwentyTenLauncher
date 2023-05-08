package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract class AbstractLauncherLanguage {

  protected final Logger logger;
  @Getter UTF8ResourceBundle bundle;

  {
    this.logger = LogManager.getLogger(this);
    this.bundle = new UTF8ResourceBundle();
  }

  public abstract void load(String baseName, String isoCode);
}
