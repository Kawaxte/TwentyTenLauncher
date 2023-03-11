package ee.twentyten.util.config;

import ee.twentyten.log.ELevel;
import ee.twentyten.util.log.LoggerUtils;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public final class CipherUtils {

  private CipherUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String encryptValue(String value) {
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
      return Base64.encodeBase64String(finalBytes);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.logMessage("Failed to encrypt value", gse, ELevel.ERROR);
    }
    return null;
  }

  public static String decryptValue(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    byte[] keyBytes = Arrays.copyOf("passwordFile".getBytes(StandardCharsets.UTF_8), 16);
    SecretKeySpec sks = new SecretKeySpec(keyBytes, "AES");

    byte[] encryptedBytes = Base64.decodeBase64(value);
    byte[] ivBytes = Arrays.copyOfRange(encryptedBytes, 0, 16);
    byte[] encryptedDataBytes = Arrays.copyOfRange(encryptedBytes, 16, encryptedBytes.length);
    IvParameterSpec ips = new IvParameterSpec(ivBytes);
    try {
      Cipher configCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      configCipher.init(Cipher.DECRYPT_MODE, sks, ips);

      byte[] decryptedBytes = configCipher.doFinal(encryptedDataBytes);
      return new String(decryptedBytes);
    } catch (GeneralSecurityException gse) {
      LoggerUtils.logMessage("Failed to decrypt value", gse, ELevel.ERROR);
    }
    return null;
  }
}
