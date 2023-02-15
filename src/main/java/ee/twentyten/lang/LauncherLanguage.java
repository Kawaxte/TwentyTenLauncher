package ee.twentyten.lang;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LauncherLanguage {

  private static ResourceBundle resourceBundle;

  public static void setLanguage(
      String tag
  ) {

    /* Get the language */
    ELanguage.getLanguage(tag);

    /* Get the name of the language file */
    String languageFileName = String.format(
        "language/locale_%s.properties",
        tag
    );

    /* Load the language file */
    try (InputStream is = LauncherLanguage.class.getClassLoader()
        .getResourceAsStream(languageFileName)) {
      Objects.requireNonNull(is, "is == null!");

      /* Create a reader for the input stream */
      Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

      /* Create a new resource bundle */
      LauncherLanguage.resourceBundle = new PropertyResourceBundle(reader);
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to load language file",
          ioe, true
      );
    }
  }

  public static String getString(
      String key
  ) {
    Objects.requireNonNull(resourceBundle, "resourceBundle == null!");
    try {
      return LauncherLanguage.resourceBundle.getString(key);
    } catch (MissingResourceException mre) {
      LoggerHelper.logError(
          "Failed to find key in bundle",
          mre, true
      );
    }
    return key;
  }
}
