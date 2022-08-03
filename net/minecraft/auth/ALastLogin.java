package net.minecraft.auth;

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
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

public class ALastLogin implements Serializable {
    private final APanel authPanel;

    public ALastLogin(APanel authPanel) {
        this.authPanel = authPanel;
    }

    public void readUsername() {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataInputStream dis = null;
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            dis = new DataInputStream(new CipherInputStream(Files.newInputStream(lastLogin.toPath()), cipher));
            for (TextField textField : Arrays.asList(authPanel.getUsernameTextField(), authPanel.getPasswordTextField())) {
                textField.setText(dis.readUTF());
            }
            authPanel.getRememberCheckbox().setState(authPanel.getPasswordTextField().getText().length() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeUsername() {
        File lastLogin = new File(MCUtils.getWorkingDirectory(), "lastlogin");
        DataOutputStream dos = null;
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            dos = new DataOutputStream(new CipherOutputStream(Files.newOutputStream(lastLogin.toPath()), cipher));
            for (String s : Arrays.asList(authPanel.getUsernameTextField().getText(),
                    authPanel.getRememberCheckbox().getState() ? authPanel.getPasswordTextField().getText() : "")) {
                dos.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Cipher getCipher(int mode) {
        try {
            Random random = new Random(43287234L);
            byte[] salt = new byte[8];
            random.nextBytes(salt);

            PBEParameterSpec ps = new PBEParameterSpec(salt, 5);
            SecretKey sk = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec("passwordfile".toCharArray()));

            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(mode, sk, ps);
            return cipher;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
}