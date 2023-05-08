package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.val;

public class AbstractLauncherLanguageImpl extends AbstractLauncherLanguage {

  @Override
  public void load(String baseName, String isoCode) {
    val languageFileName = MessageFormat.format("{0}_{1}.properties", baseName, isoCode);
    val languageFileUrl =
        Optional.ofNullable(this.getClass().getClassLoader().getResource(languageFileName));
    languageFileUrl.ifPresent(
        url -> {
          try (val is = url.openConnection().getInputStream();
              val isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            this.bundle = new UTF8ResourceBundle(isr);
          } catch (IOException ioe) {
            this.logger.error(
                "Failed to load {} from {}",
                languageFileUrl
                    .get()
                    .getFile()
                    .substring(languageFileUrl.get().getFile().lastIndexOf("/") + 1),
                languageFileUrl
                    .get()
                    .getFile()
                    .substring(0, languageFileUrl.get().getFile().lastIndexOf("/")),
                ioe);
          } finally {
            this.logger.info(
                "Loaded {} from {}",
                languageFileUrl
                    .get()
                    .getFile()
                    .substring(languageFileUrl.get().getFile().lastIndexOf("/") + 1),
                languageFileUrl
                    .get()
                    .getFile()
                    .substring(0, languageFileUrl.get().getFile().lastIndexOf("/")));
          }
        });
  }
}
