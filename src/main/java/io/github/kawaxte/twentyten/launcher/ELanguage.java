package io.github.kawaxte.twentyten.launcher;

import java.util.Objects;
import lombok.Getter;
import lombok.val;

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

  public static String getLanguage() {
    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    if (Objects.isNull(selectedLanguage)) {
      return USER_LANGUAGE;
    }
    return (String) selectedLanguage;
  }
}
