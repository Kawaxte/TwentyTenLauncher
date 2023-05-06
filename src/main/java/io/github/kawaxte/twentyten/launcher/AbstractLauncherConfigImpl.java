package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.LinkedProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.val;

public class AbstractLauncherConfigImpl extends AbstractLauncherConfig {

  @Override
  public void load() {
    val linkedProperties = new LinkedProperties();
    try (val fis = new FileInputStream(this.getConfigFilePath().toFile())) {
      linkedProperties.load(fis);

      this.getMojangAuthProperties(linkedProperties);
      this.getMicrosoftAuthProperties(linkedProperties);
      this.getOptionsProperties(linkedProperties);
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to find {}", this.getConfigFilePath().toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Failed to load {}", this.getConfigFilePath().toAbsolutePath(), ioe);
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
  public void save() {
    try (val fos = new FileOutputStream(this.getConfigFilePath().toFile())) {
      val linkedProperties = new LinkedProperties();
      this.setOptionsProperties(linkedProperties);
      this.setMicrosoftAuthProperties(linkedProperties);
      this.setMojangAuthProperties(linkedProperties);
      linkedProperties.store(fos, "TwentyTen Launcher");

      fos.flush();
    } catch (FileNotFoundException fnfe) {
      LOGGER.error("Failed to locate {}", this.getConfigFilePath().toAbsolutePath(), fnfe);
    } catch (IOException ioe) {
      LOGGER.error("Failed to save {}", this.getConfigFilePath().toAbsolutePath(), ioe);
    } finally {
      LOGGER.info(
          "Save {} to {}",
          this.getConfigFilePath().getFileName(),
          this.getConfigFilePath().toAbsolutePath().getParent());
    }
  }
}