package ee.twentyten.config;

import ee.twentyten.custom.LinkedProperties;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LauncherConfigImpl extends LauncherConfig {

  @Override
  public void load() {
    File configFile = this.getConfigFile();
    String configFilePath = configFile.getAbsolutePath();
    try (FileInputStream fis = new FileInputStream(configFile)) {
      LinkedProperties clp = new LinkedProperties();
      clp.load(fis);

      this.getGeneralProperties(clp);
      this.getYggdrasilProperties(clp);
      this.getMicrosoftProperties(clp);

      if (clp.isEmpty()) {
        this.save();
      }
      LoggerUtils.log(configFilePath, ELevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Coudn't find config file", fnfe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to load config file", ioe, ELevel.ERROR);
    }
  }

  @Override
  public void save() {
    File configFile = this.getConfigFile();
    String configFilePath = configFile.getAbsolutePath();
    try (FileOutputStream fos = new FileOutputStream(configFile)) {
      fos.write(ConfigUtils.generateConfigFileHeader().getBytes());

      LinkedProperties clpGeneral = new LinkedProperties();
      this.setGeneralProperties(clpGeneral);
      clpGeneral.store(fos, "GENERAL");

      LinkedProperties clpYggdrasil = new LinkedProperties();
      this.setYggdrasilProperties(clpYggdrasil);
      clpYggdrasil.store(fos, "MOJANG AUTHENTICATION");

      LinkedProperties clpMicrosoft = new LinkedProperties();
      this.setMicrosoftProperties(clpMicrosoft);
      clpMicrosoft.store(fos, "MICROSOFT AUTHENTICATION");

      LoggerUtils.log(configFilePath, ELevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Coudn't find config file", fnfe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to save config file", ioe, ELevel.ERROR);
    }
  }
}
