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

public class ALastLogin implements Serializable {
    private static final long serialVersionUID = 1L;
    private final APanel authPanel;

    public ALastLogin(APanel authPanel) {
        this.authPanel = authPanel;
    }

    void readUsername() {
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

    void writeUsername() {
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

    static Cipher getCipher(int mode) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec("passwordfile".toCharArray()));

            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(mode, key, new PBEParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, 20));
            return cipher;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
}