package ee.twentyten.config;

import ee.twentyten.util.CipherManager;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.codec.binary.Base64;

public class ConfigCipherImpl implements IConfigCipherService {

  @Override
  public String encrypt(String value) throws GeneralSecurityException {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = CipherManager.getSecretKey();
    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key, iv);

    byte[] encryptedByte = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    return Base64.encodeBase64String(encryptedByte);
  }

  @Override
  public String decrypt(String value) throws GeneralSecurityException {
    if (value == null || value.isEmpty()) {
      return "";
    }

    SecretKey key = CipherManager.getSecretKey();
    IvParameterSpec iv = new IvParameterSpec(key.getEncoded());

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key, iv);

    byte[] decryptedBase64 = Base64.decodeBase64(value);
    byte[] decryptedByte = cipher.doFinal(decryptedBase64);
    return new String(decryptedByte, StandardCharsets.UTF_8);
  }
}
