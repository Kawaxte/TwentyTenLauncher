package io.github.kawaxte.twentyten.launcher;

import lombok.Getter;

public enum ELanguage {
  ET("Eesti"),
  EN("English");

  public static final String USER_LANGUAGE;

  static {
    USER_LANGUAGE = System.getProperty("user.language");
  }

  @Getter private final String languageName;

  ELanguage(String languageName) {
    this.languageName = languageName;
  }
}
