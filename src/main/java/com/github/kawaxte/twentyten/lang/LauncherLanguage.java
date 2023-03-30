package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.custom.UTF8ResourceBundle;

public final class LauncherLanguage {

  public static UTF8ResourceBundle getUtf8Bundle() {
    return AbstractLauncherLanguageImpl.INSTANCE.getUtf8Bundle();
  }

  public static void load(String baseName, String isoCode) {
    AbstractLauncherLanguageImpl.INSTANCE.load(baseName, isoCode);
  }
}
