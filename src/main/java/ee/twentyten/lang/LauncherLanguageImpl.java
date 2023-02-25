package ee.twentyten.lang;

import ee.twentyten.log.ELogger;
import ee.twentyten.util.LoggerUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
    try {
      String value = this.locale.getString(key);
      if (value.isEmpty()) {
        ResourceBundle defaultLocale = ResourceBundle.getBundle("language/locale_en");
        value = defaultLocale.getString(key);
      }
      return value;
    } catch (MissingResourceException mre) {
      LoggerUtils.log("Failed to find key", mre, ELogger.ERROR);
      return key;
    }
  }

  @Override
  public void load(String code) {
    if (code == null) {
      code = "en";
    }
    ELanguage.getLanguage(code);

    String localeFileName = String.format("language/locale_%s.properties", code);
    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(localeFileName)) {
      Objects.requireNonNull(is, "is == null!");
      try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.locale = new PropertyResourceBundle(isr);

        URL localeFileInput = this.getClass().getClassLoader().getResource(localeFileName);
        Objects.requireNonNull(localeFileInput, "localeFileInput == null!");
        LoggerUtils.log(localeFileInput.getPath(), ELogger.INFO);
      } catch (IOException ioe2) {
        LoggerUtils.log("Failed to create InputStreamReader", ioe2, ELogger.ERROR);
      }
    } catch (IOException ioe1) {
      LoggerUtils.log(String.format("Failed to load locale file: %s", localeFileName), ioe1,
          ELogger.ERROR);
    }
  }
}
