package io.github.kawaxte.twentyten;

import java.util.Arrays;
import lombok.Getter;

public enum ELanguage {
  ET("Eesti"),
  EN("English");

  public static final String USER_LANGUAGE;

  static {
    USER_LANGUAGE = System.getProperty("user.language");
  }

  @Getter private final String language;

  ELanguage(String language) {
    this.language = language;
  }

  public static ELanguage getLanguage(String isoCode) {
    return Arrays.stream(ELanguage.values())
        .filter(language -> language.name().equalsIgnoreCase(isoCode))
        .findFirst()
        .orElse(null);
  }
}
