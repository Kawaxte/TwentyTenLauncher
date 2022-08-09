package net.minecraft.launcher.auth;

import net.minecraft.MCUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class AuthLastLogin {
    public final String username;
    public final String accessToken;
    public final String clientToken;
    public final String uuid;

    private AuthLastLogin(String username, String clientToken, String accessToken, String uuid) {
        this.username = username;
        this.clientToken = clientToken;
        this.accessToken = accessToken;
        this.uuid = uuid;
    }

    public static void deleteLastLogin() {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        boolean delete = lastLogin.delete();
        if(!delete) {
            lastLogin.deleteOnExit();
        }
    }

    public static void writeLastLogin(String username, String clientToken, String accessToken, String uuid) {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataOutputStream dos = null;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            dos = new DataOutputStream(new CipherOutputStream(Files.newOutputStream(lastLogin.toPath()), cipher));
            dos.writeUTF(username);
            dos.writeUTF(AuthPanel.getRememberCheckbox().getState() ? AuthPanel.getPasswordTextField().getText(): "");
            dos.writeUTF(clientToken);
            dos.writeUTF(accessToken);
            dos.writeUTF(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static AuthLastLogin readLastLogin() {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataInputStream dis = null;
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            dis = new DataInputStream(new CipherInputStream(Files.newInputStream(lastLogin.toPath()), cipher));

            String username = dis.readUTF();
            String password = dis.readUTF();
            String clientToken = dis.readUTF();
            String accessToken = dis.readUTF();
            if(accessToken.length() > 0 && username.length() > 0) {
                AuthPanel.getUsernameTextField().setText(username);
                AuthPanel.getPasswordTextField().setText(password);
                return new AuthLastLogin(username, clientToken, accessToken, "");
            }
            AuthPanel.getRememberCheckbox().setState(AuthPanel.getPasswordTextField().getText().length() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isValid() {
        return accessToken.length() > 0 && username.length() > 0;
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    private static Cipher getCipher(int mode) throws Exception {
        Random rand = new Random(43287234L);
        byte[] salt = new byte[8];
        rand.nextBytes(salt);

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = skf.generateSecret(new PBEKeySpec("passwordfile".toCharArray(), salt, 1000, 64 * 8));

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, key, new PBEParameterSpec(salt, 1000));
        return cipher;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
