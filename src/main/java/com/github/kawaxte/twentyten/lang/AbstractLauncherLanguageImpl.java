package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import com.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.var;

public class AbstractLauncherLanguageImpl extends AbstractLauncherLanguage {

  public static final AbstractLauncherLanguageImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherLanguageImpl();
  }

  @Override
  public void loadLanguage(String baseName, String isoCode) {
    var languageFilePath = Paths.get(MessageFormat.format("{0}_{1}.properties",
        baseName,
        isoCode));
    var languageFileUrl = Optional.ofNullable(this.getClass()
        .getClassLoader()
        .getResource(languageFilePath.toString()));

    languageFileUrl.ifPresent(url -> {
      try (var is = url.openConnection().getInputStream();
          var isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.utf8Bundle = new UTF8ResourceBundle(isr);

        LauncherUtils.logger.info("File \"{}\" loaded",
            languageFilePath.getFileName());
      } catch (IOException ioe) {
        LauncherUtils.logger.error("File \"{}\" could not be loaded",
            languageFilePath.getFileName(),
            ioe);
      }
    });
    languageFileUrl.orElseThrow(() -> new RuntimeException(
        MessageFormat.format("File \"{0}\" could not be found",
            languageFilePath.getFileName())));
  }
}
