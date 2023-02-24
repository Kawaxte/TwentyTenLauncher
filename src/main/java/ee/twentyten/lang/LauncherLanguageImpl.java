package ee.twentyten.lang;

import ee.twentyten.log.ELogger;
import ee.twentyten.util.LoggerUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import lombok.Getter;

public class LauncherLanguageImpl extends LauncherLanguage {

  @Getter
  private ResourceBundle locale;

  public String getString(String key) {
    if (key == null) {
      return "";
    }

    try {
      key = this.locale.getString(key);
    } catch (MissingResourceException mre) {
      LoggerUtils.log("Failed to find key", mre, ELogger.ERROR);
    }
    return key;
  }

  @Override
  public void load(String code) {
    ELanguage.getLanguage(code);

    String localeFileName = String.format("language/locale_%s.properties", code);
    try (InputStream is = LauncherLanguageImpl.class.getClassLoader()
        .getResourceAsStream(localeFileName)) {
      Objects.requireNonNull(is, "is == null!");
      try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.locale = new PropertyResourceBundle(isr);

        LoggerUtils.log(String.format("Loaded locale file: %s", localeFileName), ELogger.INFO);
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to load locale file", ioe, ELogger.ERROR);
    }
  }
}
