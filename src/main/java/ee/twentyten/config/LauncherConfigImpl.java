package ee.twentyten.config;

import ee.twentyten.custom.LinkedProperties;
import ee.twentyten.log.ELoggerLevel;
import ee.twentyten.util.CipherUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LauncherConfigImpl extends LauncherConfig {

  /* GENERAL */
  private String selectedLanguage;
  private boolean isShowBetaVersionsSelected;
  private boolean isShowAlphaVersionsSelected;
  private boolean isShowInfdevVersionsSelected;
  private String selectedVersion;
  private String clientToken;

  /* YGGDRASIL AUTHENTICATION */
  private String yggdrasilUsername;
  private String yggdrasilPassword;
  private boolean isYggdrasilPasswordSaved;
  private String yggdrasilAccessToken;
  private String yggdrasilProfileId;
  private String yggdrasilProfileName;

  /* MICROSOFT AUTHENTICATION */
  private String microsoftAccessToken;
  private int microsoftAccessTokenExpiresIn;
  private String microsoftRefreshToken;
  private String microsoftProfileId;
  private String microsoftProfileName;
  private String yggdrasilSessionId;
  private String microsoftSessionId;

  private File getConfigFile() {
    File configFile = new File(LauncherUtils.workingDirectory, "twentyten_settings.properties");
    if (!configFile.exists()) {
      try {
        boolean isFileCreated = configFile.createNewFile();
        if (!isFileCreated) {
          LoggerUtils.log("Couldn't create config file", ELoggerLevel.ERROR);
        }
      } catch (IOException ioe) {
        LoggerUtils.log("Failed to create config file", ioe, ELoggerLevel.ERROR);
      }
    }
    return configFile;
  }

  private void getGeneralProperties(LinkedProperties clp) {
    this.selectedLanguage = clp.getProperty("selectedLanguage", "en");
    this.isShowBetaVersionsSelected = Boolean.parseBoolean(
        clp.getProperty("isShowBetaVersionsSelected", "true"));
    this.isShowAlphaVersionsSelected = Boolean.parseBoolean(
        clp.getProperty("isShowAlphaVersionsSelected", "false"));
    this.isShowInfdevVersionsSelected = Boolean.parseBoolean(
        clp.getProperty("isShowInfdevVersionsSelected", "false"));
    this.selectedVersion = clp.getProperty("selectedVersion", "b1.1_02");
    this.clientToken = clp.getProperty("clientToken", ConfigUtils.generateClientToken());
  }

  private void getYggdrasilProperties(LinkedProperties clp) {
    this.yggdrasilUsername = clp.getProperty("yggdrasilUsername", null);
    this.yggdrasilPassword = CipherUtils.decryptValue(clp.getProperty("yggdrasilPassword", null));
    this.isYggdrasilPasswordSaved = Boolean.parseBoolean(
        clp.getProperty("isYggdrasilPasswordSaved", "false"));
    this.yggdrasilAccessToken = clp.getProperty("yggdrasilAccessToken", null);
    this.yggdrasilProfileId = clp.getProperty("yggdrasilProfileId", null);
    this.yggdrasilProfileName = clp.getProperty("yggdrasilProfileName", null);
    this.yggdrasilSessionId = clp.getProperty("yggdrasilSessionId");
  }

  private void getMicrosoftProperties(LinkedProperties clp) {
    this.microsoftAccessToken = clp.getProperty("microsoftAccessToken", null);
    this.microsoftAccessTokenExpiresIn = Integer.parseInt(
        clp.getProperty("microsoftAccessTokenExpiresIn", Integer.toString(0)));
    this.microsoftRefreshToken = clp.getProperty("microsoftRefreshToken", null);
    this.microsoftProfileId = clp.getProperty("microsoftProfileId", null);
    this.microsoftProfileName = clp.getProperty("microsoftProfileName", null);
    this.microsoftSessionId = clp.getProperty("microsoftSessionId");
  }

  private void setGeneralProperties(LinkedProperties clp) {
    clp.setProperty("selectedLanguage", this.selectedLanguage);
    clp.setProperty("isShowBetaVersionsSelected",
        Boolean.toString(this.isShowBetaVersionsSelected));
    clp.setProperty("isShowAlphaVersionsSelected",
        Boolean.toString(this.isShowAlphaVersionsSelected));
    clp.setProperty("isShowInfdevVersionsSelected",
        Boolean.toString(this.isShowInfdevVersionsSelected));
    clp.setProperty("selectedVersion", this.selectedVersion);
    clp.setProperty("clientToken", this.clientToken);
  }

  private void setYggdrasilProperties(LinkedProperties clp) {
    clp.setProperty("yggdrasilUsername", this.yggdrasilUsername);
    clp.setProperty("yggdrasilPassword", CipherUtils.encryptValue(this.yggdrasilPassword));
    clp.setProperty("isYggdrasilPasswordSaved", Boolean.toString(this.isYggdrasilPasswordSaved));
    clp.setProperty("yggdrasilAccessToken", this.yggdrasilAccessToken);
    clp.setProperty("yggdrasilProfileId", this.yggdrasilProfileId);
    clp.setProperty("yggdrasilProfileName", this.yggdrasilProfileName);
    clp.setProperty("yggdrasilSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.yggdrasilAccessToken,
            this.yggdrasilProfileId));
  }

  private void setMicrosoftProperties(LinkedProperties clp) {
    clp.setProperty("microsoftAccessToken", this.microsoftAccessToken);
    clp.setProperty("microsoftAccessTokenExpiresIn",
        Integer.toString(this.microsoftAccessTokenExpiresIn));
    clp.setProperty("microsoftRefreshToken", this.microsoftRefreshToken);
    clp.setProperty("microsoftProfileId", this.microsoftProfileId);
    clp.setProperty("microsoftProfileName", this.microsoftProfileName);
    clp.setProperty("microsoftSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.microsoftAccessToken,
            this.microsoftProfileId));
  }

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
      LoggerUtils.log(configFilePath, ELoggerLevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Coudn't find config file", fnfe, ELoggerLevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to load config file", ioe, ELoggerLevel.ERROR);
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

      LoggerUtils.log(configFilePath, ELoggerLevel.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Coudn't find config file", fnfe, ELoggerLevel.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to save config file", ioe, ELoggerLevel.ERROR);
    }
  }
}
