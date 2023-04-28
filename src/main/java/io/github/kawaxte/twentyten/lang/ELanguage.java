package io.github.kawaxte.twentyten.lang;

import java.util.Arrays;
import lombok.Getter;

public enum ELanguage {
  ET("Eesti"),
  EN("English");

  @Getter private final String name;

  ELanguage(String name) {
    this.name = name;
  }

  public static ELanguage getLanguage(String isoCode) {
    return Arrays.stream(ELanguage.values())
        .filter(language -> language.name().equalsIgnoreCase(isoCode))
        .findFirst()
        .orElse(null);
  }
}
