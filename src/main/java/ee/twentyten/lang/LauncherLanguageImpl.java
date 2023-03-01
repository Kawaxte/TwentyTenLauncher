package ee.twentyten.lang;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.log.ELoggerLevel;
import ee.twentyten.util.LoggerUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.Getter;

public class LauncherLanguageImpl extends LauncherLanguage {

  @Getter
  private UTF8ResourceBundle bundle;

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

  @Override
  public void load(String baseName, String isoCode) {
    ELanguage.getLanguage(isoCode);

    String localeFileName = MessageFormat.format("{0}_{1}.properties", baseName, isoCode);
    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(localeFileName)) {
      Objects.requireNonNull(is, "is == null!");
      try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.bundle = new UTF8ResourceBundle(isr);
      } catch (IOException ioe2) {
        LoggerUtils.log("Failed to load locale file", ioe2, ELoggerLevel.ERROR);
      }

      URL localeFileInput = this.getClass().getClassLoader().getResource(localeFileName);
      Objects.requireNonNull(localeFileInput, "localeFileInput == null!");
      LoggerUtils.log(localeFileInput.getPath(), ELoggerLevel.INFO);
    } catch (IOException ioe1) {
      LoggerUtils.log("Failed to load locale file", ioe1, ELoggerLevel.ERROR);
    }
  }
}
