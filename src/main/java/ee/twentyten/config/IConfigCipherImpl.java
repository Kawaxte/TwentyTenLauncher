package ee.twentyten.config;

import java.security.GeneralSecurityException;

interface IConfigCipherImpl {

  String encrypt(String value) throws GeneralSecurityException;

  String decrypt(String value) throws GeneralSecurityException;

}
