package ee.twentyten.util;

import ee.twentyten.config.ConfigCipherImpl;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class CipherManager {

  private static final ConfigCipherImpl AES_CIPHER;

  static {
    AES_CIPHER = new ConfigCipherImpl();
  }

  private CipherManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static SecretKey getSecretKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(128, new SecureRandom("passwordfile".getBytes(StandardCharsets.UTF_8)));
    return new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
  }

  public static String encryptValue(String value) {
    try {
      return AES_CIPHER.encrypt(value);
    } catch (GeneralSecurityException gse) {
      LoggingManager.logError(CipherManager.class, "Failed to encrypt value", gse);
    }
    return value;
  }

  public static String decryptValue(String value) {
    try {
      return AES_CIPHER.decrypt(value);
    } catch (GeneralSecurityException gse) {
      LoggingManager.logError(CipherManager.class, "Failed to decrypt value", gse);
    }
    return value;
  }
}
