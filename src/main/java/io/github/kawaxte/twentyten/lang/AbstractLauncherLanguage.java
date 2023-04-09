package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;

public abstract class AbstractLauncherLanguage {

  static {
    LauncherUtils.logger = LogManager.getLogger(AbstractLauncherLanguage.class);
  }

  @Getter
  UTF8ResourceBundle utf8Bundle;

  {
    this.utf8Bundle = new UTF8ResourceBundle();
  }

  public abstract void loadLanguage(String baseName, String isoCode);
}
