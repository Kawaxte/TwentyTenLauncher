package ee.twentyten.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public final class CipherManager {

  private CipherManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static SecretKey getSecretKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(128, new SecureRandom("passwordfile".getBytes(StandardCharsets.UTF_8)));
      return new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to generate secret key", e);
    }
  }

  public static String encrypt(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    try {
      SecretKey key = CipherManager.getSecretKey();
      IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);

      byte[] encryptedByte = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      return Base64.encodeBase64String(encryptedByte);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
             InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException |
             IllegalBlockSizeException e) {
      throw new RuntimeException("Failed to encrypt value", e);
    }
  }

  public static String decrypt(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    try {
      SecretKey key = CipherManager.getSecretKey();
      IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      byte[] decryptedBase64 = Base64.decodeBase64(value);
      byte[] decryptedByte = cipher.doFinal(decryptedBase64);
      return new String(decryptedByte, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
             InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException |
             IllegalBlockSizeException e) {
      throw new RuntimeException("Failed to decrypt value", e);
    }
  }
}
