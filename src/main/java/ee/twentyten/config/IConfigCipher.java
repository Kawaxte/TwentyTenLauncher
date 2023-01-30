package ee.twentyten.config;

import java.security.GeneralSecurityException;

interface IConfigCipher {

  String encrypt(String value) throws GeneralSecurityException;

  String decrypt(String value) throws GeneralSecurityException;

}
