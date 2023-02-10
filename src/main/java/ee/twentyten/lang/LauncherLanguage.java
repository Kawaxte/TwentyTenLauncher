package ee.twentyten.lang;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import lombok.Getter;

public class LauncherLanguage {

  @Getter
  private ResourceBundle bundle;

  public void setLanguage(String tag) {
    ELanguage.language = ELanguage.getLanguage(tag);

    try (InputStream is = getClass().getClassLoader()
        .getResourceAsStream(String.format("language/locale_%s.properties", tag));
        Reader reader = new InputStreamReader(Objects.requireNonNull(is, "in == null!"),
            StandardCharsets.UTF_8)) {
      this.bundle = new PropertyResourceBundle(reader);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to load language file", ioe, true);
    }
  }

  public String getString(String key) {
    return this.bundle.getString(key);
  }
}
