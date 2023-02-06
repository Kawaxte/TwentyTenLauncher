package ee.twentyten.config;

import ee.twentyten.util.LogHelper;
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

  private static final Class<LauncherCipher> CLASS_REF;

  static {
    CLASS_REF = LauncherCipher.class;
  }

  private static SecretKey generateSecretKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(128, new SecureRandom("passwordfile".getBytes(StandardCharsets.UTF_8)));
      return new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
    } catch (NoSuchAlgorithmException nsae) {
      LogHelper.logError(CLASS_REF, "Failed to get instance of KeyGenerator", nsae);
    }
    return null;
  }

  static String encryptValue(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = generateSecretKey();
    Objects.requireNonNull(key, "key == null!");

    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);

      byte[] encryptedByte = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      return Base64.encodeBase64String(encryptedByte);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LogHelper.logError(CLASS_REF, "Failed to get instance of Cipher", two_e1);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LogHelper.logError(CLASS_REF, "Failed to initialise Cipher", two_e2);
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LogHelper.logError(CLASS_REF, "Failed to encrypt value", two_e3);
    }
    return null;
  }

  static String decryptValue(String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = generateSecretKey();
    Objects.requireNonNull(key, "key == null!");

    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      byte[] decodedBytes = Base64.decodeBase64(value);
      byte[] decryptedByte = cipher.doFinal(decodedBytes);
      return new String(decryptedByte, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LogHelper.logError(CLASS_REF, "Failed to get instance of Cipher", two_e1);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LogHelper.logError(CLASS_REF, "Failed to initialise Cipher", two_e2);
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LogHelper.logError(CLASS_REF, "Failed to encrypt value", two_e3);
    }
    return null;
  }
}
