package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractLauncherConfigImpl extends AbstractLauncherConfig {

  public static final AbstractLauncherConfigImpl INSTANCE;
  static Logger logger;

  static {
    INSTANCE = new AbstractLauncherConfigImpl();

    logger = LogManager.getLogger(AbstractLauncherConfigImpl.class);
  }

  @Override
  public void loadConfig() throws IOException {
    val linkedProperties = new LinkedProperties();
    try (val fis = new FileInputStream(this.getConfigFilePath().toFile())) {
      linkedProperties.load(fis);

      this.getYggdrasilLoginProperties(linkedProperties);
      this.getMicrosoftLoginProperties(linkedProperties);
      this.getOptionsProperties(linkedProperties);
    } catch (FileNotFoundException fnfe) {
      logger.warn("Config file '{}' not found",
          this.getConfigFilePath().getFileName());
    } finally {
      if (!linkedProperties.isEmpty()) {
        logger.info("Loading config file '{}'",
            this.getConfigFilePath().getFileName());
      } else {
        this.saveConfig();
      }
    }
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
      logger.error("Config file '{}' not found",
          this.getConfigFilePath().getFileName(),
          fnfe);
    } finally {
      logger.info("Saving config file '{}'",
          this.getConfigFilePath().getFileName());
    }
  }
}
