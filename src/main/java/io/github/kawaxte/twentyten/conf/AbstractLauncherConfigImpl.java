package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
      logger.error("Failed to locate {}",
          this.getConfigFilePath().toAbsolutePath(),
          fnfe);
    } finally {
      if (!linkedProperties.isEmpty()) {
        logger.info("Loading {} from {}",
            this.getConfigFilePath().getFileName(),
            this.getConfigFilePath().toAbsolutePath().getParent());
      } else {
        this.saveConfig();
      }
    }
  }

  @Override
  public void saveConfig() throws IOException {
    try (val fos = new FileOutputStream(this.getConfigFilePath().toFile())) {
      val linkedProperties = new LinkedProperties();
      this.setOptionsProperties(linkedProperties);
      this.setMicrosoftLoginProperties(linkedProperties);
      this.setYggdrasilLoginProperties(linkedProperties);
      linkedProperties.store(fos, "TwentyTen Launcher");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      logger.error("Failed to locate {}",
          this.getConfigFilePath().toAbsolutePath(),
          fnfe);
    } finally {
      logger.info("Saving {} to {}",
          this.getConfigFilePath().getFileName(),
          this.getConfigFilePath().toAbsolutePath().getParent());
    }
  }
}
