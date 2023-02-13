package ee.twentyten.lang;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LauncherLanguage {

  private static ResourceBundle resourceBundle;

  /**
   * Sets the language of the application.
   *
   * @param tag The tag of the language to set
   */
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

      /* Check if the input stream is null */
      if (is == null) {

        /* Create a throwable */
        Throwable t = new Throwable("Failed to return input stream");

        /* Log the error */
        LoggerHelper.logError(t.getMessage(), t, true);
        return;
      }


      /* Create a reader */
      Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

      /* Load the language file */
      LauncherLanguage.resourceBundle = new PropertyResourceBundle(reader);
    } catch (IOException ioe) {

      /* Create a string for the error */
      String loadError = "Failed to load language file";

      /* Log the error */
      LoggerHelper.logError(loadError, ioe, true);
    }
  }

  /**
   * Returns the string value of the specified key from the resource bundle.
   *
   * @param key The key to retrieve the string value for.
   * @return The string value of the specified key, or the key itself if the key
   * cannot be found in the resource bundle.
   */
  public static String getString(
      String key
  ) {

    /* Check if the bundle is null */
    if (LauncherLanguage.resourceBundle == null) {

      /* Create a throwable */
      Throwable t = new Throwable("Failed to return resource bundle");

      /* Log the error */
      LoggerHelper.logError(t.getMessage(), t, true);
      return key;
    }

    /* Try to get the string value of the key */
    try {
      return LauncherLanguage.resourceBundle.getString(key);
    } catch (MissingResourceException mre) {

      /* Create a string for the error */
      String findError = "Failed to find key in bundle";

      /* Log the error */
      LoggerHelper.logError(findError, mre, true);
    }
    
    return key;
  }
}
