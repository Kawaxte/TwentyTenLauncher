package ee.twentyten.lang;

import lombok.Getter;

public enum ELanguage {
  LANGUAGE_CS("Čeština"),
  LANGUAGE_DE("Deutsch"),
  LANGUAGE_ET("Eesti"),
  LANGUAGE_EN("English"),
  LANGUAGE_FI("Suomi"),
  LANGUAGE_FR("Français"),
  LANGUAGE_HU("Magyar"),
  LANGUAGE_JA("日本語"),
  LANGUAGE_PL("Polski");

  @Getter
  private final String name;

  ELanguage(String name) {
    this.name = name;
  }

  public static void getLanguage(
      String lang
  ) {

    /* Loop through all the languages and check if the language code is the
     * same as the one passed in. */
    for (ELanguage language : values()) {
      String languageCode = language.name().substring(9);
      if (languageCode.equalsIgnoreCase(lang)) {
        return;
      }
    }
  }
}
