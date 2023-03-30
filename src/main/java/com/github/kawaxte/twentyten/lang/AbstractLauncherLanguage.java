package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.custom.UTF8ResourceBundle;
import lombok.Getter;

public abstract class AbstractLauncherLanguage {

  @Getter
  UTF8ResourceBundle utf8Bundle;

  {
    this.utf8Bundle = new UTF8ResourceBundle();
  }

  public abstract void load(String baseName, String isoCode);
}
