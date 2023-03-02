package ee.twentyten.lang;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.LoggerUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;

public class LauncherLanguageImpl extends LauncherLanguage {

  @Override
  public void load(String baseName, String isoCode) {
    ELanguage.getLanguage(isoCode);

    String localeFileName = MessageFormat.format("{0}_{1}.properties", baseName, isoCode);
    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(localeFileName)) {
      Objects.requireNonNull(is, "is == null!");
      try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.bundle = new UTF8ResourceBundle(isr);
      } catch (IOException ioe2) {
        LoggerUtils.log("Failed to load locale file", ioe2, ELevel.ERROR);
      }

      URL localeFileInput = this.getClass().getClassLoader().getResource(localeFileName);
      Objects.requireNonNull(localeFileInput, "localeFileInput == null!");
      LoggerUtils.log(localeFileInput.getPath(), ELevel.INFO);
    } catch (IOException ioe1) {
      LoggerUtils.log("Failed to load locale file", ioe1, ELevel.ERROR);
    }
  }
}
