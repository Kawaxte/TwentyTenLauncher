package ee.twentyten.lang;

import lombok.Getter;
import lombok.Setter;

public enum ELanguage {
  LANGUAGE_CS("Čeština"),
  LANGUAGE_DE("Deutsch"),
  LANGUAGE_ET("Eesti"),
  LANGUAGE_EN("English"),
  LANGUAGE_FR("Français"),
  LANGUAGE_HU("Magyar"),
  LANGUAGE_JP("日本語"),
  LANGUAGE_PL("Polski");

  @Setter
  public static ELanguage language;
  @Getter
  private final String name;

  ELanguage(String name) {
    this.name = name;
  }

  public static ELanguage getLanguage(String lang) {
    for (ELanguage language : values()) {
      if (language.name().substring(9).equalsIgnoreCase(lang)) {
        return language;
      }
    }
    return LANGUAGE_EN;
  }
}
