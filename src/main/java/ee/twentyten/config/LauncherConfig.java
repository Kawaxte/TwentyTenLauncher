package ee.twentyten.config;

import ee.twentyten.custom.CustomLinkedProperties;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LoggerHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LauncherConfig extends LauncherCipher {

  public static LauncherConfig instance;
  private String username;
  private String password;
  private Boolean passwordSaved;
  private String clientToken;
  private String accessToken;
  private String refreshToken;
  private String profileId;
  private String profileName;
  private Boolean usingBeta;
  private Boolean usingAlpha;
  private Boolean usingInfdev;
  private String selectedVersion;
  private String selectedLanguage;

  public static LauncherConfig loadConfig() {
    LauncherConfig config;
    try {
      File configFile = new File(FileHelper.workingDirectory,
          "twentyten.properties");
      if (!configFile.exists()) {
        boolean isConfigFileCreated = configFile.createNewFile();
        if (!isConfigFileCreated) {
          LoggerHelper.logError("Failed to create config file", true);
          return null;
        }
      }

      try (FileInputStream fis = new FileInputStream(configFile)) {
        CustomLinkedProperties properties = new CustomLinkedProperties();
        properties.load(fis);

        config = new LauncherConfig();
        config.username = properties.getProperty("username");
        config.password = LauncherConfig.decryptValue(
            properties.getProperty("password"));
        config.passwordSaved = Boolean.parseBoolean(
            properties.getProperty("password-saved"));

        config.clientToken = properties.getProperty("client-token");
        config.accessToken = LauncherConfig.decryptValue(
            properties.getProperty("access-token"));
        config.refreshToken = LauncherConfig.decryptValue(
            properties.getProperty("refresh-token"));
        config.profileId = properties.getProperty("profile-id");
        config.profileName = properties.getProperty("profile-name");

        config.usingBeta = Boolean.parseBoolean(
            properties.getProperty("using-beta"));
        config.usingAlpha = Boolean.parseBoolean(
            properties.getProperty("using-alpha"));
        config.usingInfdev = Boolean.parseBoolean(
            properties.getProperty("using-infdev"));
        config.selectedVersion = properties.getProperty("selected-version");
        config.selectedLanguage = properties.getProperty("selected-language");

        LoggerHelper.logInfo(
            String.format("\"%s\"", configFile.getAbsolutePath()), true);

        return config;
      } catch (IOException ioe2) {
        LoggerHelper.logError("Failed to load config file", ioe2, true);
      }
    } catch (IOException ioe1) {
      LoggerHelper.logError("Failed to get working directory", ioe1, true);
    }
    return null;
  }

  public String getSessionId() {
    return String.format("%s:%s:%s", this.clientToken, this.accessToken,
        this.profileId);
  }

  public void saveConfig() {
    String username = this.username != null ? this.username : "";
    String password = this.password != null ? this.password : "";
    String passwordSaved =
        this.passwordSaved != null ? this.passwordSaved.toString() : "";
    String clientToken = this.clientToken != null ? this.clientToken : "";
    String accessToken = this.accessToken != null ? this.accessToken : "";
    String refreshToken = this.refreshToken != null ? this.refreshToken : "";
    String profileId = this.profileId != null ? this.profileId : "";
    String profileName = this.profileName != null ? this.profileName : "";
    String usingBeta = this.usingBeta != null ? this.usingBeta.toString() : "";
    String usingAlpha =
        this.usingAlpha != null ? this.usingAlpha.toString() : "";
    String usingInfdev =
        this.usingInfdev != null ? this.usingInfdev.toString() : "";
    String selectedVersion =
        this.selectedVersion != null ? this.selectedVersion : "";
    String selectedLanguage =
        this.selectedLanguage != null ? this.selectedLanguage : "";

    CustomLinkedProperties general = new CustomLinkedProperties();
    general.setProperty("username", username);
    general.setProperty("password", LauncherConfig.encryptValue(password));
    general.setProperty("password-saved", passwordSaved);

    CustomLinkedProperties profile = new CustomLinkedProperties();
    profile.setProperty("client-token", clientToken);
    profile.setProperty("access-token",
        LauncherConfig.encryptValue(accessToken));
    profile.setProperty("refresh-token",
        LauncherConfig.encryptValue(refreshToken));
    profile.setProperty("profile-id", profileId);
    profile.setProperty("profile-name", profileName);

    CustomLinkedProperties options = new CustomLinkedProperties();
    options.setProperty("using-beta", usingBeta);
    options.setProperty("using-alpha", usingAlpha);
    options.setProperty("using-infdev", usingInfdev);
    options.setProperty("selected-version", selectedVersion);
    options.setProperty("selected-language", selectedLanguage);

    File configFile = new File(FileHelper.workingDirectory,
        "twentyten.properties");
    try (FileOutputStream fos = new FileOutputStream(
        configFile.getAbsolutePath())) {

      String lineSeparator = System.lineSeparator();

      String configFileHeader =
          "##################################################"
              + lineSeparator
              + "##    TwentyTen Launcher Configuration File     ##"
              + lineSeparator
              + "##==============================================##"
              + lineSeparator
              + "##    This file is automatically generated by   ##"
              + lineSeparator
              + "##    the launcher. Do not modify this file.    ##"
              + lineSeparator
              + "##################################################"
              + lineSeparator;
      fos.write(configFileHeader.getBytes());
      general.store(fos, "GENERAL ---------------------------------------|");
      profile.store(fos, "PROFILE ---------------------------------------|");
      options.store(fos, "OPTIONS ---------------------------------------|");

      LoggerHelper.logInfo(
          String.format("\"%s\"", configFile.getAbsolutePath()), true);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to save config file", ioe, true);
    }
  }
}
