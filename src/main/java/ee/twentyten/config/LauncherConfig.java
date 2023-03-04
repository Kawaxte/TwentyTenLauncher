package ee.twentyten.config;

import ee.twentyten.custom.LinkedProperties;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.CipherUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class LauncherConfig {

  // General
  String selectedLanguage;
  boolean isShowBetaVersionsSelected;
  boolean isShowAlphaVersionsSelected;
  boolean isShowInfdevVersionsSelected;
  String selectedVersion;
  String clientToken;

  // Yggdrasil Authentication
  String yggdrasilUsername;
  String yggdrasilPassword;
  boolean isYggdrasilPasswordSaved;
  String yggdrasilAccessToken;
  String yggdrasilProfileName;
  String yggdrasilProfileId;
  String yggdrasilSessionId;

  // Microsoft Authentication
  String microsoftAccessToken;
  long microsoftAccessTokenExpiresIn;
  String microsoftRefreshToken;
  long microsoftRefreshTokenExpiresIn;
  String microsoftProfileName;
  String microsoftProfileId;
  String microsoftSessionId;

  File getConfigFile() {
    File configFile = new File(LauncherUtils.workingDirectory, "twentyten_settings.properties");
    if (!configFile.exists()) {
      try {
        boolean isFileCreated = configFile.createNewFile();
        if (!isFileCreated) {
          throw new IOException("Failed to create config file");
        }
      } catch (IOException ioe) {
        LoggerUtils.logMessage(ioe.getMessage(), ioe, ELevel.ERROR);
      }
    }
    return configFile;
  }

  void getGeneralProperties(LinkedProperties clp) {
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

  void getYggdrasilProperties(LinkedProperties clp) {
    this.yggdrasilUsername = clp.getProperty("yggdrasilUsername", null);
    this.yggdrasilPassword = CipherUtils.decryptValue(clp.getProperty("yggdrasilPassword", null));
    this.isYggdrasilPasswordSaved = Boolean.parseBoolean(
        clp.getProperty("isYggdrasilPasswordSaved", "false"));
    this.yggdrasilAccessToken = clp.getProperty("yggdrasilAccessToken", null);
    this.yggdrasilProfileName = clp.getProperty("yggdrasilProfileName", null);
    this.yggdrasilProfileId = clp.getProperty("yggdrasilProfileId", null);
    this.yggdrasilSessionId = clp.getProperty("yggdrasilSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.yggdrasilAccessToken,
            this.yggdrasilProfileId));
  }

  void getMicrosoftProperties(LinkedProperties clp) {
    this.microsoftAccessToken = clp.getProperty("microsoftAccessToken", null);
    this.microsoftAccessTokenExpiresIn = Long.parseLong(
        clp.getProperty("microsoftAccessTokenExpiresIn", String.valueOf(-1)));
    this.microsoftRefreshToken = clp.getProperty("microsoftRefreshToken", null);
    this.microsoftRefreshTokenExpiresIn = Long.parseLong(
        clp.getProperty("microsoftRefreshTokenExpiresIn", String.valueOf(-1)));
    this.microsoftProfileName = clp.getProperty("microsoftProfileName", null);
    this.microsoftProfileId = clp.getProperty("microsoftProfileId", null);
    this.microsoftSessionId = clp.getProperty("microsoftSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.yggdrasilAccessToken,
            this.yggdrasilProfileId));
  }

  void setGeneralProperties(LinkedProperties clp) {
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

  void setYggdrasilProperties(LinkedProperties clp) {
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

  void setMicrosoftProperties(LinkedProperties clp) {
    clp.setProperty("microsoftAccessToken", this.microsoftAccessToken);
    clp.setProperty("microsoftAccessTokenExpiresIn",
        Long.toString(this.microsoftAccessTokenExpiresIn));
    clp.setProperty("microsoftRefreshToken", this.microsoftRefreshToken);
    clp.setProperty("microsoftRefreshTokenExpiresIn",
        Long.toString(this.microsoftRefreshTokenExpiresIn));
    clp.setProperty("microsoftProfileId", this.microsoftProfileId);
    clp.setProperty("microsoftProfileName", this.microsoftProfileName);
    clp.setProperty("microsoftSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.microsoftAccessToken,
            this.microsoftProfileId));
  }

  public abstract void read();

  public abstract void write();
}
