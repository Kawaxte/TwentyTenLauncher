package com.github.kawaxte.twentyten.lang;

import com.github.kawaxte.twentyten.custom.UTF8ResourceBundle;
import com.github.kawaxte.twentyten.log.LauncherLogger;
import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

public class AbstractLauncherLanguageImpl extends AbstractLauncherLanguage {

  public static final AbstractLauncherLanguageImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherLanguageImpl();
  }

  @Override
  public void load(String baseName, String isoCode) {
    Path languageFile = Paths.get(MessageFormat.format("{0}_{1}.properties", baseName, isoCode));

    Optional<URL> input = Optional.ofNullable(this.getClass().getClassLoader().getResource(
        languageFile.toString()));
    input.ifPresent(url -> {
      try (InputStream is = url.openConnection().getInputStream();
          InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
        this.utf8Bundle = new UTF8ResourceBundle(isr);

        LauncherLogger.log(ELevel.INFO, "Loaded language: {0}", input.get().toString());
      } catch (IOException ioe) {
        LauncherLogger.log(ioe, "{0} could not be loaded", languageFile);
      }
    });
    input.orElseThrow(() -> new IllegalArgumentException(MessageFormat.format(
        "{0} could not be found", languageFile)));
  }
}
