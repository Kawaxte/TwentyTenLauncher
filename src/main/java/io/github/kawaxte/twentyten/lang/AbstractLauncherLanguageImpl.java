package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.lang.LauncherLanguage.ELanguage;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractLauncherLanguageImpl extends AbstractLauncherLanguage {

  public static final AbstractLauncherLanguageImpl INSTANCE;
  static Logger logger;

  static {
    INSTANCE = new AbstractLauncherLanguageImpl();

    logger = LogManager.getLogger(AbstractLauncherLanguageImpl.class);
  }

  @Override
  public void loadLanguage(String baseName, String isoCode) {
    val languageFilePath = Paths.get(MessageFormat.format("{0}_{1}.properties",
        baseName,
        isoCode));
    val languageFileUrl = Optional.ofNullable(this.getClass()
        .getClassLoader()
        .getResource(languageFilePath.toString()));
    languageFileUrl.ifPresent(url -> {
      try (val is = url.openConnection().getInputStream();
          val isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.utf8Bundle = new UTF8ResourceBundle(isr);
      } catch (IOException ioe) {
        logger.error("Failed to load language file '{}'",
            ELanguage.getLanguage(isoCode).getName(),
            ioe);
      } finally {
        logger.info("Using '{}' as language",
            ELanguage.getLanguage(isoCode).getName());
      }
    });
  }
}
