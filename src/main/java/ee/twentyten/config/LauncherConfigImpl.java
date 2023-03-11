package ee.twentyten.config;

import ee.twentyten.custom.LinkedProperties;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.log.LoggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LauncherConfigImpl extends LauncherConfig {

  @Override
  public void read() {
    File configFile = this.getConfigFile();
    String configFilePath = configFile.getAbsolutePath();
    try (FileInputStream fis = new FileInputStream(configFile)) {
      LinkedProperties clp = new LinkedProperties();
      clp.load(fis);

      this.getGeneralProperties(clp);
      this.getYggdrasilProperties(clp);
      this.getMicrosoftProperties(clp);

      if (clp.isEmpty()) {
        this.write();
      }
      LoggerUtils.logMessage(configFilePath, ELevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.logMessage("Coudn't find config file", fnfe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to write config file", ioe, ELevel.ERROR);
    }
  }

  @Override
  public void write() {
    File configFile = this.getConfigFile();
    String configFilePath = configFile.getAbsolutePath();
    try (FileOutputStream fos = new FileOutputStream(configFile)) {
      fos.write(ConfigUtils.generateConfigFileHeader().getBytes());

      LinkedProperties clpGeneral = new LinkedProperties();
      this.setGeneralProperties(clpGeneral);
      clpGeneral.store(fos, "GENERAL");

      LinkedProperties clpYggdrasil = new LinkedProperties();
      this.setYggdrasilProperties(clpYggdrasil);
      clpYggdrasil.store(fos, "YGGDRASIL AUTHENTICATION");

      LinkedProperties clpMicrosoft = new LinkedProperties();
      this.setMicrosoftProperties(clpMicrosoft);
      clpMicrosoft.store(fos, "MICROSOFT AUTHENTICATION");

      LoggerUtils.logMessage(configFilePath, ELevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.logMessage("Coudn't find config file", fnfe, ELevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to read config file", ioe, ELevel.ERROR);
    }
  }
}
