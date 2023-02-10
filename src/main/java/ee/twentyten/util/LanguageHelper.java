package ee.twentyten.util;

import ee.twentyten.lang.ELanguage;
import ee.twentyten.lang.LauncherLanguage;

public final class LanguageHelper {

  private static final LauncherLanguage LANGUAGE;

  static {
    LANGUAGE = new LauncherLanguage();
  }

  private LanguageHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }


  public static String getString(String key) {
    return LANGUAGE.getString(key);
  }

  public static void getLanguage(String tag) {
    ELanguage.getLanguage(tag);
  }

  public static void setLanguage(String tag) {
    LANGUAGE.setLanguage(tag);
  }
}
