package ee.twentyten.config;

import ee.twentyten.custom.CustomLinkedProperties;
import ee.twentyten.log.ELogger;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.IOException;

abstract class LauncherConfig {

  String clientToken;
  String selectedVersion;
  String selectedLanguage;
  String yggdrasilUsername;
  String yggdrasilPassword;
  boolean isYggdrasilPasswordSaved;
  String yggdrasilAccessToken;
  String yggdrasilProfileId;
  String yggdrasilProfileName;
  int microsoftAccessTokenExpiresIn;
  String microsoftAccessToken;
  String microsoftRefreshToken;
  String microsoftProfileId;
  String microsoftProfileName;
  String yggdrasilSessionId;
  String microsoftSessionId;

  File getConfigFile() {
    File configFile = new File(LauncherUtils.workingDirectory, "twentyten_settings.properties");
    if (!configFile.exists()) {
      boolean isConfigFileCreated;
      try {
        isConfigFileCreated = configFile.createNewFile();
        if (!isConfigFileCreated) {
          throw new IOException("Failed to create config file");
        }
      } catch (IOException ioe) {
        LoggerUtils.log(ioe.getMessage(), ioe, ELogger.ERROR);
      }
    }
    return configFile;
  }

  void getGeneralProperties(CustomLinkedProperties clp) {
    this.clientToken = clp.getProperty("clientToken");
    this.selectedVersion = clp.getProperty("selectedVersion");
    this.selectedLanguage = clp.getProperty("selectedLanguage");
  }

  void getYggdrasilProperties(CustomLinkedProperties clp) {
    this.yggdrasilUsername = clp.getProperty("yggdrasilUsername");
    this.yggdrasilPassword = this.decrypt(clp.getProperty("yggdrasilPassword"));
    this.isYggdrasilPasswordSaved = Boolean.parseBoolean(
        clp.getProperty("isYggdrasilPasswordSaved "));
    this.yggdrasilAccessToken = clp.getProperty("yggdrasilAccessToken");
    this.yggdrasilProfileId = clp.getProperty("yggdrasilProfileId");
    this.yggdrasilProfileName = clp.getProperty("yggdrasilProfileName");
    this.yggdrasilSessionId = clp.getProperty("yggdrasilSessionId");
  }

  void getMicrosoftProperties(CustomLinkedProperties clp) {
    this.microsoftAccessTokenExpiresIn = Integer.parseInt(
        clp.getProperty("microsoftAccessTokenExpiresIn") != null ? clp.getProperty(
            "microsoftAccessTokenExpiresIn") : "0");
    this.microsoftAccessToken = clp.getProperty("microsoftAccessToken");
    this.microsoftRefreshToken = clp.getProperty("microsoftRefreshToken");
    this.microsoftProfileId = clp.getProperty("microsoftProfileId");
    this.microsoftProfileName = clp.getProperty("microsoftProfileName");
    this.microsoftSessionId = clp.getProperty("microsoftSessionId");
  }

  void setGeneralProperties(CustomLinkedProperties clp) {
    clp.setProperty("clientToken",
        this.clientToken != null ? this.clientToken : ConfigUtils.generateClientToken());
    clp.setProperty("selectedVersion",
        this.selectedVersion != null ? this.selectedVersion : "b1.1_02");
    clp.setProperty("selectedLanguage",
        this.selectedLanguage != null ? this.selectedLanguage : "en");
  }

  void setYggdrasilProperties(CustomLinkedProperties clp) {
    clp.setProperty("yggdrasilUsername",
        this.yggdrasilUsername != null ? this.yggdrasilUsername : "");
    clp.setProperty("yggdrasilPassword",
        this.encrypt(this.yggdrasilPassword != null ? this.yggdrasilPassword : ""));
    clp.setProperty("isYggdrasilPasswordSaved", Boolean.toString(this.isYggdrasilPasswordSaved));
    clp.setProperty("yggdrasilAccessToken",
        this.yggdrasilAccessToken != null ? this.yggdrasilAccessToken : "");
    clp.setProperty("yggdrasilProfileId",
        this.yggdrasilProfileId != null ? this.yggdrasilProfileId : "");
    clp.setProperty("yggdrasilProfileName",
        this.yggdrasilProfileName != null ? this.yggdrasilProfileName : "");
    clp.setProperty("yggdrasilSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.yggdrasilAccessToken,
            this.yggdrasilProfileId));
  }

  void setMicrosoftProperties(CustomLinkedProperties clp) {
    clp.setProperty("microsoftAccessTokenExpiresIn", Integer.toString(
        this.microsoftAccessTokenExpiresIn != 0 ? this.microsoftAccessTokenExpiresIn : 0));
    clp.setProperty("microsoftAccessToken",
        this.microsoftAccessToken != null ? this.microsoftAccessToken : "");
    clp.setProperty("microsoftRefreshToken",
        this.microsoftRefreshToken != null ? this.microsoftRefreshToken : "");
    clp.setProperty("microsoftProfileId",
        this.microsoftProfileId != null ? this.microsoftProfileId : "");
    clp.setProperty("microsoftProfileName",
        this.microsoftProfileName != null ? this.microsoftProfileName : "");
    clp.setProperty("microsoftSessionId",
        ConfigUtils.formatSessionId(this.clientToken, this.microsoftAccessToken,
            this.microsoftProfileId));
  }

  abstract void load();

  abstract void save();

  abstract String encrypt(String value);

  abstract String decrypt(String value);
}
