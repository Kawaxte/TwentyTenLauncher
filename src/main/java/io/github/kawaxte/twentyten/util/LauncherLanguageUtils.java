package io.github.kawaxte.twentyten.util;

import java.util.Arrays;
import lombok.Getter;

public final class LauncherLanguageUtils {

  public enum ELanguage {
    EN("English");

    @Getter
    private final String name;

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
}
