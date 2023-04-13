package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    val languageFileName = MessageFormat.format("{0}_{1}.properties",
        baseName,
        isoCode);
    val languageFileUrl = Optional.ofNullable(this.getClass()
        .getClassLoader()
        .getResource(languageFileName));
    languageFileUrl.ifPresent(url -> {
      try (val is = url.openConnection().getInputStream();
          val isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.utf8Bundle = new UTF8ResourceBundle(isr);
      } catch (IOException ioe) {
        logger.error("Failed to load {} from {}",
            languageFileName,
            languageFileUrl.get()
                .getPath()
                .substring(0, languageFileUrl.get()
                    .getPath()
                    .lastIndexOf("/")),
            ioe);
      } finally {
        logger.info("Loading {} from {}",
            languageFileName,
            languageFileUrl.get()
                .getPath()
                .substring(0, languageFileUrl.get()
                    .getPath()
                    .lastIndexOf("/")));
      }
    });
  }
}
