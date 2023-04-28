package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle.UTF8Control;
import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import lombok.val;

public final class LauncherLanguage {

  public static Map<String, String> languageLookup;

  private LauncherLanguage() {}

  public static UTF8ResourceBundle getUtf8Bundle() {
    return AbstractLauncherLanguageImpl.INSTANCE.getUtf8Bundle();
  }

  public static UTF8ResourceBundle getUtf8Bundle(String isoCode) {
    return isoCode != null
        ? (UTF8ResourceBundle)
            UTF8ResourceBundle.getBundle(
                "messages", Locale.forLanguageTag(isoCode), new UTF8Control())
        : (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages", new UTF8Control());
  }

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values())
        .forEachOrdered(
            language -> {
              defaultComboBoxModel.addElement(language.getName());
              languageLookup.put(language.getName(), language.toString().toLowerCase());
            });

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    languageLookup.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), selectedLanguage))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }

  public static void loadLanguage(String baseName, String isoCode) {
    AbstractLauncherLanguageImpl.INSTANCE.loadLanguage(baseName, isoCode);
  }
}
