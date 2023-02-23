package ee.twentyten.config;

import ee.twentyten.custom.CustomLinkedProperties;
import ee.twentyten.log.ELogger;
import ee.twentyten.util.ConfigUtils;
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

public class LauncherConfigImpl extends LauncherConfig {

  @Override
  public void load() {
    File configFile = this.getConfigFile();
    String configFilePath = configFile.getAbsolutePath();
    try (FileInputStream fis = new FileInputStream(configFile)) {
      CustomLinkedProperties clp = new CustomLinkedProperties();
      clp.load(fis);

      this.getGeneralProperties(clp);
      this.getYggdrasilProperties(clp);
      this.getMicrosoftProperties(clp);
      if (clp.isEmpty()) {
        this.save();
      }

      LoggerUtils.log(String.format("Loaded config file: %s", configFilePath), ELogger.INFO);
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

      CustomLinkedProperties clpGeneral = new CustomLinkedProperties();
      this.setGeneralProperties(clpGeneral);
      clpGeneral.store(fos, "GENERAL");

      CustomLinkedProperties clpYggdrasil = new CustomLinkedProperties();
      this.setYggdrasilProperties(clpYggdrasil);
      clpYggdrasil.store(fos, "MOJANG AUTHENTICATION");

      CustomLinkedProperties clpMicrosoft = new CustomLinkedProperties();
      this.setMicrosoftProperties(clpMicrosoft);
      clpMicrosoft.store(fos, "MICROSOFT AUTHENTICATION");

      LoggerUtils.log(String.format("Saved config file: %s", configFilePath), ELogger.INFO);
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
      Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
      instance.init(Cipher.ENCRYPT_MODE, sks, ips);

      byte[] encryptedBytes = instance.doFinal(value.getBytes());
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
      Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
      instance.init(Cipher.DECRYPT_MODE, sks, ips);

      byte[] decryptedBytes = instance.doFinal(encryptedDataBytes);
      return new String(decryptedBytes);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.log("Failed to decrypt value", gse, ELogger.ERROR);
    }
    return null;
  }
}
