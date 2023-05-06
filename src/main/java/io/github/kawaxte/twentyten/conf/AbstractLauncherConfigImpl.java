package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.val;

public class AbstractLauncherConfigImpl extends AbstractLauncherConfig {

  public static final AbstractLauncherConfigImpl INSTANCE;

  static {
    INSTANCE = new AbstractLauncherConfigImpl();
  }

  @Override
  public void load() throws IOException {
    val linkedProperties = new LinkedProperties();
    try (val fis = new FileInputStream(this.getConfigFilePath().toFile())) {
      linkedProperties.load(fis);

      this.getMojangAuthProperties(linkedProperties);
      this.getMicrosoftAuthProperties(linkedProperties);
      this.getOptionsProperties(linkedProperties);
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to locate {}", this.getConfigFilePath().toAbsolutePath(), fnfe);
    } finally {
      if (!linkedProperties.isEmpty()) {
        LOGGER.info(
            "Load {} from {}",
            this.getConfigFilePath().getFileName(),
            this.getConfigFilePath().toAbsolutePath().getParent());
      } else {
        this.save();
      }
    }
  }

  @Override
  public void save() throws IOException {
    try (val fos = new FileOutputStream(this.getConfigFilePath().toFile())) {
      val linkedProperties = new LinkedProperties();
      this.setOptionsProperties(linkedProperties);
      this.setMicrosoftAuthProperties(linkedProperties);
      this.setMojangAuthProperties(linkedProperties);
      linkedProperties.store(fos, "TwentyTen Launcher");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to locate {}", this.getConfigFilePath().toAbsolutePath(), fnfe);
    } finally {
      LOGGER.info(
          "Save {} to {}",
          this.getConfigFilePath().getFileName(),
          this.getConfigFilePath().toAbsolutePath().getParent());
    }
  }
}
