package io.github.kawaxte.twentyten.conf;

import io.github.kawaxte.twentyten.misc.LinkedProperties;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
abstract class AbstractLauncherConfig {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(AbstractLauncherConfig.class);
  }

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
  String mojangUsername;
  String mojangPassword;
  boolean mojangRememberPasswordChecked;
  String mojangProfileId;
  String mojangProfileName;
  boolean mojangProfileDemo;
  String mojangAccessToken;
  String mojangClientToken;

  Path getConfigFilePath() throws IOException {
    val configFilePath =
        Paths.get(
            String.valueOf(LauncherUtils.WORKING_DIR_PATH),
            MessageFormat.format(
                "{0}_{1}.properties", "twentyten", System.getProperty("user.name")));
    val configFile = configFilePath.toFile();
    if (!configFile.exists() && !configFile.createNewFile()) {
      throw new IOException();
    }
    return configFilePath;
  }

  void getOptionsProperties(LinkedProperties properties) {
    this.selectedLanguage = properties.getProperty("selectedLanguage", "en");
    this.showBetaVersionsSelected =
        Boolean.parseBoolean(properties.getProperty("showBetaVersionsSelected", "true"));
    this.showAlphaVersionsSelected =
        Boolean.parseBoolean(properties.getProperty("showAlphaVersionsSelected", "false"));
    this.showInfdevVersionsSelected =
        Boolean.parseBoolean(properties.getProperty("showInfdevVersionsSelected", "false"));
    this.selectedVersion = properties.getProperty("selectedVersion", "b1.1_02");
  }

  void getMicrosoftAuthProperties(LinkedProperties properties) {
    this.microsoftProfileId = properties.getProperty("microsoftProfileId", "");
    this.microsoftProfileName = properties.getProperty("microsoftProfileName", "");
    this.microsoftProfileDemo =
        Boolean.parseBoolean(properties.getProperty("microsoftProfileDemo", "false"));
    this.microsoftAccessToken = properties.getProperty("microsoftAccessToken", "");
    this.microsoftAccessTokenExpiresIn =
        Long.parseLong(properties.getProperty("microsoftAccessTokenExpiresIn", "0"));
    this.microsoftRefreshToken = properties.getProperty("microsoftRefreshToken", "");
    this.microsoftClientToken =
        properties.getProperty(
            "microsoftClientToken", UUID.randomUUID().toString().replaceAll("-", ""));
  }

  void getMojangAuthProperties(LinkedProperties properties) {
    this.mojangUsername = properties.getProperty("mojangUsername", "");
    this.mojangPassword = properties.getProperty("mojangPassword", "");
    this.mojangRememberPasswordChecked =
        Boolean.parseBoolean(properties.getProperty("mojangRememberPasswordChecked", "false"));
    this.mojangProfileId = properties.getProperty("mojangProfileId", "");
    this.mojangProfileName = properties.getProperty("mojangProfileName", "");
    this.mojangProfileDemo =
        Boolean.parseBoolean(properties.getProperty("mojangProfileDemo", "false"));
    this.mojangAccessToken = properties.getProperty("mojangAccessToken", "");
    this.mojangClientToken =
        properties.getProperty(
            "mojangClientToken", UUID.randomUUID().toString().replaceAll("-", ""));
  }

  void setMojangAuthProperties(LinkedProperties properties) {
    properties.setProperty("mojangUsername", this.mojangUsername);
    properties.setProperty("mojangPassword", this.mojangPassword);
    properties.setProperty(
        "mojangRememberPasswordChecked", Boolean.toString(this.mojangRememberPasswordChecked));
    properties.setProperty("mojangProfileId", this.mojangProfileId);
    properties.setProperty("mojangProfileName", this.mojangProfileName);
    properties.setProperty("mojangProfileDemo", Boolean.toString(this.mojangProfileDemo));
    properties.setProperty("mojangAccessToken", this.mojangAccessToken);
    properties.setProperty("mojangClientToken", this.mojangClientToken);
  }

  void setMicrosoftAuthProperties(LinkedProperties properties) {
    properties.setProperty("microsoftProfileId", this.microsoftProfileId);
    properties.setProperty("microsoftProfileName", this.microsoftProfileName);
    properties.setProperty("microsoftProfileDemo", Boolean.toString(this.microsoftProfileDemo));
    properties.setProperty("microsoftAccessToken", this.microsoftAccessToken);
    properties.setProperty(
        "microsoftAccessTokenExpiresIn", Long.toString(this.microsoftAccessTokenExpiresIn));
    properties.setProperty("microsoftRefreshToken", this.microsoftRefreshToken);
    properties.setProperty("microsoftClientToken", this.microsoftClientToken);
  }

  void setOptionsProperties(LinkedProperties properties) {
    properties.setProperty("selectedLanguage", this.selectedLanguage);
    properties.setProperty(
        "showBetaVersionsSelected", Boolean.toString(this.showBetaVersionsSelected));
    properties.setProperty(
        "showAlphaVersionsSelected", Boolean.toString(this.showAlphaVersionsSelected));
    properties.setProperty(
        "showInfdevVersionsSelected", Boolean.toString(this.showInfdevVersionsSelected));
    properties.setProperty("selectedVersion", this.selectedVersion);
  }

  public abstract void load() throws IOException;

  public abstract void save() throws IOException;
}
