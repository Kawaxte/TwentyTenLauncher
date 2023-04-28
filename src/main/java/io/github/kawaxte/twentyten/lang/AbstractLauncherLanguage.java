package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import lombok.Getter;

abstract class AbstractLauncherLanguage {

  @Getter UTF8ResourceBundle utf8Bundle;

  {
    this.utf8Bundle = new UTF8ResourceBundle();
  }

  public abstract void loadLanguage(String baseName, String isoCode);
}
