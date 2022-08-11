package net.minecraft.launcher.auth;

import net.minecraft.MCUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

public class AuthLastLogin {
    public final String accessToken;
    public final String uuid;

    private AuthLastLogin(String accessToken, String uuid) {
        this.accessToken = accessToken;
        this.uuid = uuid;
    }

    public static void writeLastLogin(String accessToken, String uuid) {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataOutputStream dos = null;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            dos = new DataOutputStream(new CipherOutputStream(Files.newOutputStream(lastLogin.toPath()), cipher));
            for (String s : Arrays.asList(AuthPanel.getUsernameTextField().getText(),
                    AuthPanel.getRememberCheckbox().getState() ? AuthPanel.getPasswordTextField().getText() : "", accessToken, uuid)) {
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
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            dis = new DataInputStream(new CipherInputStream(Files.newInputStream(lastLogin.toPath()), cipher));
            for (TextField textField : Arrays.asList(AuthPanel.getUsernameTextField(), AuthPanel.getPasswordTextField())) {
                textField.setText(dis.readUTF());
            }

            String accessToken = dis.readUTF();
            if (accessToken.isEmpty()) {
                return null;
            }
            AuthPanel.getRememberCheckbox().setState(AuthPanel.getPasswordTextField().getText().length() > 0);
        } catch (Exception e) {
            System.err.println("Failed to read lastlogin from " + lastLogin.getAbsolutePath());
            return null;
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

    public static void deleteLastLogin() {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        if (lastLogin.exists()) {
            boolean delete = lastLogin.delete();
            if (!delete) {
                System.err.println("Failed to delete lastlogin from " + lastLogin.getAbsolutePath());
            }
        }
    }

    public boolean isValidForMicrosoft() {
        return accessToken.length() > 0 && AuthPanel.getUsernameTextField().getText().equalsIgnoreCase("$MS");
    }

    public boolean isValidForYggdrasil() {
        return accessToken.length() > 0 && AuthPanel.getUsernameTextField().getText().length() > 0;
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
