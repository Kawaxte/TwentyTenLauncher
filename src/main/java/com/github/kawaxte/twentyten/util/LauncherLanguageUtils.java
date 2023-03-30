package com.github.kawaxte.twentyten.util;

import com.github.kawaxte.twentyten.custom.UTF8ResourceBundle;
import com.github.kawaxte.twentyten.custom.UTF8ResourceBundle.UTF8Control;
import java.awt.Container;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import lombok.Getter;

public class LauncherLanguageUtils {

  private LauncherLanguageUtils() {
    throw new Error(MessageFormat.format("{0} is not instantiable", this.getClass().getName()));
  }

  public static void setTextToComponent(Locale locale, Container c, String key, Object... args) {
    UTF8ResourceBundle bundle = (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages",
        locale, new UTF8Control());
    if (c instanceof JFrame) {
      ((JFrame) c).setTitle(args != null
          ? MessageFormat.format(bundle.getString(key), args)
          : bundle.getString(key));
    }
    if (c instanceof JDialog) {
      ((JDialog) c).setTitle(args != null
          ? MessageFormat.format(bundle.getString(key), args)
          : bundle.getString(key));
    }
    if (c instanceof JLabel) {
      ((JLabel) c).setText(args != null
          ? MessageFormat.format(bundle.getString(key), args)
          : bundle.getString(key));
    }
    if (c instanceof AbstractButton) {
      ((AbstractButton) c).setText(args != null
          ? MessageFormat.format(bundle.getString(key), args)
          : bundle.getString(key));
    }
  }

  public enum ELanguage {
    BG("Български"),
    CS("Čeština"),
    DE("Deutsch"),
    ET("Eesti"),
    EN("English"),
    FI("Suomi"),
    FR("Français"),
    HU("Magyar"),
    JA("日本語"),
    PL("Polski");

    @Getter
    private final String name;

    ELanguage(String name) {
      this.name = name;
    }

    public static ELanguage getLanguage(String lang) {
      return Arrays.stream(values()).filter(language
          -> language.name.equalsIgnoreCase(lang)).findFirst().orElse(null);
    }
  }
}
