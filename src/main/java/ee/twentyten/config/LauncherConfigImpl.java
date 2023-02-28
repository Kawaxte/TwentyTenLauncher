package ee.twentyten.config;

import ee.twentyten.custom.LinkedProperties;
import ee.twentyten.log.ELogger;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
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
    this.yggdrasilPassword = this.decrypt(clp.getProperty("yggdrasilPassword", null));
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
    clp.setProperty("yggdrasilPassword", this.encrypt(this.yggdrasilPassword));
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
      LoggerUtils.log(configFilePath, ELogger.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Failed to find config file", fnfe, ELogger.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to load config file", ioe, ELogger.ERROR);
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

      LoggerUtils.log(configFilePath, ELogger.INFO);
    } catch (FileNotFoundException fnfe) {
      LoggerUtils.log("Failed to find config file", fnfe, ELogger.ERROR);
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to save config file", ioe, ELogger.ERROR);
    }
  }

  @Override
  public String encrypt(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    byte[] keyBytes = Arrays.copyOf("passwordFile".getBytes(StandardCharsets.UTF_8), 16);
    SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");

    SecureRandom sr = new SecureRandom();
    byte[] ivBytes = new byte[16];
    sr.nextBytes(ivBytes);

    IvParameterSpec ips = new IvParameterSpec(ivBytes);
    try {
      Cipher configCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      configCipher.init(Cipher.ENCRYPT_MODE, sks, ips);

      byte[] encryptedBytes = configCipher.doFinal(value.getBytes());
      byte[] finalBytes = new byte[ivBytes.length + encryptedBytes.length];
      System.arraycopy(ivBytes, 0, finalBytes, 0, ivBytes.length);
      System.arraycopy(encryptedBytes, 0, finalBytes, ivBytes.length, encryptedBytes.length);
      return DatatypeConverter.printBase64Binary(finalBytes);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.log("Failed to encrypt value", gse, ELogger.ERROR);
    }
    return null;
  }

  @Override
  public String decrypt(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    byte[] keyBytes = Arrays.copyOf("passwordFile".getBytes(StandardCharsets.UTF_8), 16);
    SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");

    byte[] encryptedBytes = DatatypeConverter.parseBase64Binary(value);
    byte[] ivBytes = Arrays.copyOfRange(encryptedBytes, 0, 16);
    byte[] encryptedDataBytes = Arrays.copyOfRange(encryptedBytes, 16, encryptedBytes.length);
    IvParameterSpec ips = new IvParameterSpec(ivBytes);
    try {
      Cipher configCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      configCipher.init(Cipher.DECRYPT_MODE, sks, ips);

      byte[] decryptedBytes = configCipher.doFinal(encryptedDataBytes);
      return new String(decryptedBytes);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.log("Failed to decrypt value", gse, ELogger.ERROR);
    }
    return null;
  }
}
