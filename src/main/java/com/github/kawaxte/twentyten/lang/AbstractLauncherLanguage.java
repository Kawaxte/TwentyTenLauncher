package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import com.github.kawaxte.twentyten.util.LauncherUtils;
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
