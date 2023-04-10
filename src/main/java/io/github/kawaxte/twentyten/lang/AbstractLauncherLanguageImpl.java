package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.util.LauncherLanguageUtils.ELanguage;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.val;

public class AbstractLauncherLanguageImpl extends AbstractLauncherLanguage {

  public static final AbstractLauncherLanguageImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherLanguageImpl();
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
        LauncherUtils.logger.error("Failed to load '{}' language file for '{}'",
            languageFilePath.getFileName(),
            ELanguage.getLanguage(isoCode).getName(),
            ioe);
      } finally {
        LauncherUtils.logger.info("Using '{}' as language file for '{}'",
            languageFilePath.getFileName(),
            ELanguage.getLanguage(isoCode).getName());
      }
    });
  }
}
