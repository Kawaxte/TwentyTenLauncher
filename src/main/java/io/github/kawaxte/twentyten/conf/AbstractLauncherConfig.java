package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
abstract class AbstractLauncherConfig {

  String selectedLanguage;
  boolean showBetaVersionsSelected;
  boolean showAlphaVersionsSelected;
  boolean showInfdevVersionsSelected;
  String selectedVersion;
  String microsoftProfileId;
  String microsoftProfileName;
  boolean microsoftProfileDemo;
  String microsoftAccessToken;
  long microsoftAccessTokenExpiresIn;
  String microsoftRefreshToken;
  String microsoftClientToken;
  String yggdrasilUsername;
  String yggdrasilPassword;
  boolean yggdrasilRememberPasswordChecked;
  String yggdrasilProfileId;
  String yggdrasilProfileName;
  boolean yggdrasilProfileLegacy;
  boolean yggdrasilProfileDemo;
  String yggdrasilAccessToken;
  String yggdrasilClientToken;

  static Logger logger;

  static {
    logger = LogManager.getLogger(AbstractLauncherConfig.class);
  }

  Path getConfigFilePath() throws IOException {
    var configFilePath = Paths.get(String.valueOf(LauncherUtils.WORKING_DIR_PATH),
        MessageFormat.format("{0}_{1}.properties",
            "twentyten", System.getProperty("user.name")));
    var configFile = configFilePath.toFile();
    if (!configFile.exists() && !configFile.createNewFile()) {
      throw new IOException();
    }
    return configFilePath;
  }

  void getOptionsProperties(LinkedProperties properties) {
    this.selectedLanguage = properties.getProperty("selectedLanguage",
        "en");
    this.showBetaVersionsSelected = Boolean.parseBoolean(
        properties.getProperty("showBetaVersionsSelected",
            String.valueOf(true)));
    this.showAlphaVersionsSelected = Boolean.parseBoolean(
        properties.getProperty("showAlphaVersionsSelected",
            String.valueOf(false)));
    this.showInfdevVersionsSelected = Boolean.parseBoolean(
        properties.getProperty("showInfdevVersionsSelected",
            String.valueOf(false)));
    this.selectedVersion = properties.getProperty("selectedVersion",
        "b1.1_02");
  }

  void getMicrosoftLoginProperties(LinkedProperties properties) {
    this.microsoftProfileId = properties.getProperty("microsoftProfileId",
        "");
    this.microsoftProfileName = properties.getProperty("microsoftProfileName",
        "");
    this.microsoftProfileDemo = Boolean.parseBoolean(
        properties.getProperty("microsoftProfileDemo",
            String.valueOf(false)));
    this.microsoftAccessToken = properties.getProperty("microsoftAccessToken",
        "");
    this.microsoftAccessTokenExpiresIn = Long.parseLong(
        properties.getProperty("microsoftAccessTokenExpiresIn",
            String.valueOf(0L)));
    this.microsoftRefreshToken = properties.getProperty("microsoftRefreshToken",
        "");
    this.microsoftClientToken = properties.getProperty("microsoftClientToken",
        "");
  }

  void getYggdrasilLoginProperties(LinkedProperties properties) {
    this.yggdrasilUsername = properties.getProperty("yggdrasilUsername",
        "");
    this.yggdrasilPassword = properties.getProperty("yggdrasilPassword",
        "");
    this.yggdrasilRememberPasswordChecked = Boolean.parseBoolean(
        properties.getProperty("yggdrasilRememberPasswordChecked",
            String.valueOf(false)));
    this.yggdrasilProfileId = properties.getProperty("yggdrasilProfileId",
        "");
    this.yggdrasilProfileName = properties.getProperty("yggdrasilProfileName",
        "");
    this.yggdrasilProfileLegacy = Boolean.parseBoolean(
        properties.getProperty("yggdrasilProfileLegacy",
            String.valueOf(false)));
    this.yggdrasilProfileDemo = Boolean.parseBoolean(
        properties.getProperty("yggdrasilProfileDemo",
            String.valueOf(false)));
    this.yggdrasilAccessToken = properties.getProperty("yggdrasilAccessToken",
        "");
    this.yggdrasilClientToken = properties.getProperty("yggdrasilClientToken",
        "");
  }

  void setYggdrasilLoginProperties(LinkedProperties properties) {
    properties.setProperty("yggdrasilUsername",
        this.yggdrasilUsername != null
            ? this.yggdrasilUsername
            : "");
    properties.setProperty("yggdrasilPassword",
        this.yggdrasilPassword != null
            ? this.yggdrasilPassword
            : "");
    properties.setProperty("yggdrasilRememberPasswordChecked",
        String.valueOf(this.yggdrasilRememberPasswordChecked));
    properties.setProperty("yggdrasilProfileId",
        this.yggdrasilProfileId != null
            ? this.yggdrasilProfileId
            : "");
    properties.setProperty("yggdrasilProfileName",
        this.yggdrasilProfileName != null
            ? this.yggdrasilProfileName
            : "");
    properties.setProperty("yggdrasilProfileLegacy",
        String.valueOf(this.yggdrasilProfileLegacy));
    properties.setProperty("yggdrasilProfileDemo",
        String.valueOf(this.yggdrasilProfileDemo));
    properties.setProperty("yggdrasilAccessToken",
        this.yggdrasilAccessToken != null
            ? this.yggdrasilAccessToken
            : "");
    properties.setProperty("yggdrasilClientToken",
        this.yggdrasilClientToken != null
            ? this.yggdrasilClientToken
            : "");
  }

  void setMicrosoftLoginProperties(LinkedProperties properties) {
    properties.setProperty("microsoftProfileId",
        this.microsoftProfileId != null
            ? this.microsoftProfileId
            : "");
    properties.setProperty("microsoftProfileName",
        this.microsoftProfileName != null
            ? this.microsoftProfileName
            : "");
    properties.setProperty("microsoftProfileDemo",
        String.valueOf(this.microsoftProfileDemo));
    properties.setProperty("microsoftAccessToken",
        this.microsoftAccessToken != null
            ? this.microsoftAccessToken
            : "");
    properties.setProperty("microsoftAccessTokenExpiresIn",
        String.valueOf(this.microsoftAccessTokenExpiresIn));
    properties.setProperty("microsoftRefreshToken",
        this.microsoftRefreshToken != null
            ? this.microsoftRefreshToken
            : "");
    properties.setProperty("microsoftClientToken",
        this.microsoftClientToken != null
            ? this.microsoftClientToken
            : "");
  }

  void setOptionsProperties(LinkedProperties properties) {
    properties.setProperty("selectedLanguage",
        this.selectedLanguage != null
            ? this.selectedLanguage
            : "en");
    properties.setProperty("showBetaVersionsSelected",
        String.valueOf(this.showBetaVersionsSelected));
    properties.setProperty("showAlphaVersionsSelected",
        String.valueOf(this.showAlphaVersionsSelected));
    properties.setProperty("showInfdevVersionsSelected",
        String.valueOf(this.showInfdevVersionsSelected));
    properties.setProperty("selectedVersion",
        this.selectedVersion != null
            ? this.selectedVersion
            : "b1.1_02");
  }

  public abstract void loadConfig() throws IOException;

  public abstract void saveConfig() throws IOException;
}
