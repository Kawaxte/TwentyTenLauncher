package ee.twentyten.config;

import ee.twentyten.util.LoggerHelper;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

abstract class LauncherCipher {

  private static SecretKey getSecretKey() {
    try {
      KeyGenerator kg = KeyGenerator.getInstance("AES");

      SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
      sr.setSeed("passwordfile".getBytes(StandardCharsets.UTF_8));

      kg.init(128, sr);
      return new SecretKeySpec(kg.generateKey().getEncoded(), "AES");
    } catch (NoSuchAlgorithmException nsae) {
      LoggerHelper.logError("Failed to get instance of KeyGenerator", nsae,
          true);
    }
    return null;
  }

  static String encryptValue(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = getSecretKey();
    Objects.requireNonNull(key, "key == null!");

    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);

      byte[] encryptedByte = cipher.doFinal(
          value.getBytes(StandardCharsets.UTF_8));
      return Base64.encodeBase64String(encryptedByte);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LoggerHelper.logError("Failed to get instance of Cipher", two_e1, true);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LoggerHelper.logError("Failed to initialise Cipher", two_e2, true);
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LoggerHelper.logError("Failed to encrypt value", two_e3, true);
    }
    return null;
  }

  static String decryptValue(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = getSecretKey();
    Objects.requireNonNull(key, "key == null!");

    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      byte[] decodedBytes = Base64.decodeBase64(value);
      byte[] decryptedByte = cipher.doFinal(decodedBytes);
      return new String(decryptedByte, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LoggerHelper.logError("Failed to get instance of Cipher", two_e1, true);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LoggerHelper.logError("Failed to initialise Cipher", two_e2, true);
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LoggerHelper.logError("Failed to encrypt value", two_e3, true);
    }
    return null;
  }
}
