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

      /* Initialise the key generator with a 128 bit key size because 128 bit
       * keys are considered to be secure enough for AES encryption. */
      KeyGenerator kg = KeyGenerator.getInstance("AES");

      /* Initialise a random number generator using the SHA1PRNG algorithm
       * because it is considered to be a secure random number generator. */
      SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

      /* Set the seed value of the random number generator to the password
       * file name. This will ensure that the same password file will always
       * generate the same key. */
      sr.setSeed("passwordfile".getBytes(StandardCharsets.UTF_8));

      /* Generate the key using the random number generator. */
      kg.init(128, sr);
      return new SecretKeySpec(kg.generateKey().getEncoded(), "AES");
    } catch (NoSuchAlgorithmException nsae) {
      LoggerHelper.logError(
          "Failed to get instance of KeyGenerator",
          nsae, true
      );
    }
    return null;
  }

  static String encryptValue(
      String value
  ) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    /* Get the secret key. */
    SecretKey key = getSecretKey();
    Objects.requireNonNull(
        key, "key == null!"
    );

    /* Create an initialisation vector (IV) using the same key. */
    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    try {

      /* Get an instance of the AES cipher in CBC mode with PKCS5 padding
       * because it is considered to be one of the most secure ciphers Java 7
       * supports out of the box. */
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      /* Initialise the cipher for encryption using the secret key and IV. */
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);

      /* Encrypt the value. */
      byte[] encryptedByte = cipher.doFinal(
          value.getBytes(StandardCharsets.UTF_8));

      /* Return the encrypted value as a Base64 encoded string because it is
       * considered to be a secure enough encoding for storing values in a
       * configuration file. */
      return Base64.encodeBase64String(encryptedByte);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LoggerHelper.logError(
          "Failed to get instance of Cipher",
          two_e1, true
      );
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LoggerHelper.logError(
          "Failed to initialise Cipher",
          two_e2, true
      );
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LoggerHelper.logError(
          "Failed to encrypt value",
          two_e3, true
      );
    }
    return null;
  }

  static String decryptValue(
      String value
  ) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    /* Get the secret key. */
    SecretKey key = getSecretKey();
    Objects.requireNonNull(
        key, "key == null!"
    );

    /* Create an initialisation vector (IV) using the same key. */
    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    try {

      /* Get an instance of the AES cipher in CBC mode with PKCS5 padding
       * because it is considered to be one of the most secure ciphers Java 7
       * supports out of the box. */
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      /* Initialise the cipher for decryption using the secret key and IV. */
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      /* Decode the Base64 encoded value. */
      byte[] decodedBytes = Base64.decodeBase64(value);

      /* Decrypt the value. */
      byte[] decryptedByte = cipher.doFinal(decodedBytes);

      /* Return the decrypted value as a string. */
      return new String(decryptedByte, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException two_e1) {
      LoggerHelper.logError(
          "Failed to get instance of Cipher",
          two_e1, true
      );
    } catch (InvalidKeyException | InvalidAlgorithmParameterException two_e2) {
      LoggerHelper.logError(
          "Failed to initialise Cipher",
          two_e2, true
      );
    } catch (IllegalBlockSizeException | BadPaddingException two_e3) {
      LoggerHelper.logError(
          "Failed to encrypt value",
          two_e3, true
      );
    }
    return null;
  }
}
