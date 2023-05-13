package io.github.kawaxte.twentyten.launcher;

import lombok.Getter;

public enum ELanguage {
  BG("Български"),
  CS("Čeština"),
  DE("Deutsch"),
  EN("English"),
  ES("Español"),
  ET("Eesti"),
  FI("Suomi"),
  FR("Français"),
  PL("Polski");

  public static final String USER_LANGUAGE;

  static {
    USER_LANGUAGE = System.getProperty("user.language");
  }

  @Getter private final String languageName;

  ELanguage(String languageName) {
    this.languageName = languageName;
  }
}
