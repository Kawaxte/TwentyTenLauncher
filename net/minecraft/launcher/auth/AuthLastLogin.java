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
import java.util.Arrays;
import java.util.Random;

public final class AuthLastLogin {
    public final String accessToken;

    private AuthLastLogin(String accessToken) {
        this.accessToken = accessToken;
    }

    public static void writeLastLogin(String accessToken) {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataOutputStream dos = null;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, "passwordfile");
            if (cipher != null) {
                dos = new DataOutputStream(new CipherOutputStream(Files.newOutputStream(lastLogin.toPath()), cipher));
            } else {
                dos = new DataOutputStream(Files.newOutputStream(lastLogin.toPath()));
            }
            for (String s : Arrays.asList(accessToken, AuthPanel.getUsernameTextField().getText(),
                    AuthPanel.getPasswordTextField().getText(),
                    AuthPanel.getRememberCheckbox().getState() ? AuthPanel.getPasswordTextField().getText() : "")) {
                dos.writeUTF(s);
            }
        } catch (Exception e) {
            System.err.println("Failed to write lastlogin to " + lastLogin.getAbsolutePath());
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
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, "passwordfile");
            if (cipher != null) {
                dis = new DataInputStream(new CipherInputStream(Files.newInputStream(lastLogin.toPath()), cipher));
            } else {
                dis = new DataInputStream(Files.newInputStream(lastLogin.toPath()));
            }
            String accessToken = dis.readUTF();
            if (accessToken.length() > 0) {
                AuthPanel.getUsernameTextField().setText(dis.readUTF());
                AuthPanel.getPasswordTextField().setText(dis.readUTF());
                if (AuthPanel.getRememberCheckbox().getState()) {
                    AuthPanel.getPasswordTextField().getText().length();
                }
                AuthPanel.getRememberCheckbox().setState(dis.readBoolean());
                return new AuthLastLogin(accessToken);
            }
        } catch (Exception e) {
            System.err.println("Failed to read lastlogin from " + lastLogin.getAbsolutePath());
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

    /**
     * ##################################################
     * # GETTERS & SETTERS #
     * ##################################################
     */
    private static Cipher getCipher(int cipherMode, String cipherString) throws Exception {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(cipherString.toCharArray());

        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(cipherMode, secretKey, pbeParamSpec);
        return cipher;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
