package ee.twentyten.config;

import java.security.GeneralSecurityException;

interface IConfigCipherService {

  String encrypt(String value) throws GeneralSecurityException;

  String decrypt(String value) throws GeneralSecurityException;

}
