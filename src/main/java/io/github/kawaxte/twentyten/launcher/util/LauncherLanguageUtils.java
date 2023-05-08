package io.github.kawaxte.twentyten.launcher.util;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;

import io.github.kawaxte.twentyten.ELanguage;
import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.UTF8ResourceBundle.UTF8Control;
import io.github.kawaxte.twentyten.launcher.AbstractLauncherLanguageImpl;
import io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.swing.DefaultComboBoxModel;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherLanguageUtils {

  public static AbstractLauncherLanguageImpl languageInstance;
  public static Map<String, String> languageLookup;

  static {
    languageInstance = new AbstractLauncherLanguageImpl();
  }

  final Logger logger;

  {
    this.logger = LogManager.getLogger(this);
  }

  private LauncherLanguageUtils() {}

  public static UTF8ResourceBundle getUTF8Bundle(String isoCode) {
    return Optional.ofNullable(isoCode)
        .map(
            code ->
                (UTF8ResourceBundle)
                    UTF8ResourceBundle.getBundle(
                        "messages", Locale.forLanguageTag(code), new UTF8Control()))
        .orElseGet(
            () -> (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages", new UTF8Control()));
  }

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values())
        .forEachOrdered(
            language -> {
              defaultComboBoxModel.addElement(language.getLanguage());
              languageLookup.put(language.getLanguage(), language.toString().toLowerCase());
            });

    val selectedLanguage = configInstance.getSelectedLanguage();
    languageLookup.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), selectedLanguage))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }
}
