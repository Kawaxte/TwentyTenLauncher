package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.UTF8ResourceBundle.UTF8Control;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherLanguage {

  private static final Logger LOGGER;

  public static UTF8ResourceBundle bundle;

  static {
    LOGGER = LogManager.getLogger(LauncherLanguage.class);

    if (Objects.isNull(bundle)) {
      bundle = new UTF8ResourceBundle();
    } else {
      throw new IllegalStateException("bundle is already initialised");
    }
  }

  private LauncherLanguage() {}

  public static UTF8ResourceBundle getUTF8Bundle(String languageCode) {
    return Optional.ofNullable(languageCode)
        .map(
            code ->
                (UTF8ResourceBundle)
                    UTF8ResourceBundle.getBundle(
                        "messages", Locale.forLanguageTag(code), new UTF8Control()))
        .orElseGet(
            () -> (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages", new UTF8Control()));
  }

  public static void loadLanguage(String baseName, String languageCode) {
    val fileName = String.format("%s_%s.properties", baseName, languageCode);
    val fileUrl =
        Optional.ofNullable(LauncherLanguage.class.getClassLoader().getResource(fileName));
    int fileUrlIndex =
        fileUrl
            .map(value -> value.getFile().lastIndexOf("/"))
            .orElseThrow(() -> new NullPointerException("fileUrl cannot be null"));
    fileUrl.ifPresent(
        url -> {
          try (val stream = url.openConnection().getInputStream();
              val streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            bundle = new UTF8ResourceBundle(streamReader);
          } catch (IOException ioe) {
            LOGGER.error(
                "Failed to load {} from {}",
                fileUrl.get().getFile().substring(fileUrlIndex + 1),
                fileUrl.get().getFile().substring(0, fileUrlIndex),
                ioe);
          } finally {
            LOGGER.info(
                "Loaded {} from {}",
                fileUrl.get().getFile().substring(fileUrlIndex + 1),
                fileUrl.get().getFile().substring(0, fileUrlIndex));
          }
        });
  }
}
