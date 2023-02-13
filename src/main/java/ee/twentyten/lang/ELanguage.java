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

    /* Set the name */
    this.name = name;
  }

  /**
   * Gets the language of the application based on the input string.
   *
   * @param lang the string representing the language code
   */
  public static void getLanguage(String lang) {

    /* Loop through all the languages */
    for (ELanguage language : values()) {

      /* Get the language code from the enum */
      String languageCode = language.name().substring(9);

      /* Check if the language code is valid */
      if (languageCode.equalsIgnoreCase(lang)) {

        /* Set the language */
        return;
      }
    }
  }
}
