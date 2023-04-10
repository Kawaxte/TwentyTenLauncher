package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.val;

public class AbstractLauncherConfigImpl extends AbstractLauncherConfig {

  public static final AbstractLauncherConfigImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherConfigImpl();
  }

  @Override
  public void loadConfig() throws IOException {
    val configFile = this.getConfigFilePath().toFile();
    if (configFile.exists() && configFile.length() > 0) {
      try (val fis = new FileInputStream(this.getConfigFilePath().toFile())) {
        val linkedProperties = new LinkedProperties();
        linkedProperties.load(fis);

        this.getYggdrasilLoginProperties(linkedProperties);
        this.getMicrosoftLoginProperties(linkedProperties);
        this.getOptionsProperties(linkedProperties);
        return;
      } catch (FileNotFoundException fnfe) {
        LauncherUtils.logger.warn("Config file '{}' not found",
            this.getConfigFilePath().getFileName());
      } finally {
        LauncherUtils.logger.info("Loading config file '{}'",
            this.getConfigFilePath().getFileName());
      }
    }
    this.saveConfig();
  }

  @Override
  public void saveConfig() throws IOException {
    try (val fos = new FileOutputStream(this.getConfigFilePath().toFile())) {
      val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
      fos.write((MessageFormat.format("# {0}", sdf.format(new Date())).getBytes()));
      fos.write(System.lineSeparator().getBytes());

      val optionsLinkedProperties = new LinkedProperties();
      this.setOptionsProperties(optionsLinkedProperties);
      optionsLinkedProperties.store(fos, "OPTIONS");

      val microsoftLoginLinkedProperties = new LinkedProperties();
      this.setMicrosoftLoginProperties(microsoftLoginLinkedProperties);
      microsoftLoginLinkedProperties.store(fos, "MICROSOFT AUTH");

      val yggdrasilLoginLinkedProperties = new LinkedProperties();
      this.setYggdrasilLoginProperties(yggdrasilLoginLinkedProperties);
      yggdrasilLoginLinkedProperties.store(fos, "YGGDRASIL AUTH");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      LauncherUtils.logger.error("Config file '{}' not found",
          this.getConfigFilePath().getFileName(),
          fnfe);
    } finally {
      LauncherUtils.logger.info("Saving config file '{}'",
          this.getConfigFilePath().getFileName());
    }
  }
}
