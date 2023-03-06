package ee.twentyten.lang;

import ee.twentyten.custom.UTF8ResourceBundle;
import lombok.Getter;

abstract class LauncherLanguage {

  @Getter
  UTF8ResourceBundle bundle;

  public final String getString(String key) {
    String value = this.bundle.getString(key);
    if (value.isEmpty()) {
      UTF8ResourceBundle defaultBundle = UTF8ResourceBundle.getCustomBundle(
          "language/locale_en");
      value = defaultBundle.getString(key);
    }
    return value;
  }

  public final String getString(UTF8ResourceBundle bundle, String key) {
    this.bundle = bundle;

    String value = this.bundle.getString(key);
    if (value.isEmpty()) {
      UTF8ResourceBundle defaultBundle = UTF8ResourceBundle.getCustomBundle(
          "language/locale_en");
      value = defaultBundle.getString(key);
    }
    return value;
  }

  public abstract void load(String baseName, String isoCode);
}
