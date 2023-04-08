package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.misc.UTF8ResourceBundle;

public final class LauncherLanguage {

  public static UTF8ResourceBundle getUtf8Bundle() {
    return AbstractLauncherLanguageImpl.INSTANCE.getUtf8Bundle();
  }

  public static void loadLanguage(String baseName, String isoCode) {
    AbstractLauncherLanguageImpl.INSTANCE.loadLanguage(baseName, isoCode);
  }
}
