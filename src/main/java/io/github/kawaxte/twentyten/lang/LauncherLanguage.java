package io.github.kawaxte.twentyten.lang;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import lombok.Getter;
import lombok.val;

public final class LauncherLanguage {

  private LauncherLanguage() {
  }

  public static Map<String, String> languageLookup;

  public static UTF8ResourceBundle getUtf8Bundle() {
    return AbstractLauncherLanguageImpl.INSTANCE.getUtf8Bundle();
  }

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values()).forEachOrdered(language -> {
      defaultComboBoxModel.addElement(language.getName());
      languageLookup.put(language.getName(), language.toString().toLowerCase());
    });
    // TODO: Rewrite this when config is re-implemented
    //    for (Map.Entry<String, String> entry : LanguageUtils.languageMap.entrySet()) {
    //      if (entry.getValue().equals(ConfigUtils.getInstance().getSelectedLanguage())) {
    //        languageModel.setSelectedItem(entry.getKey());
    //        break;
    //      }
    //    }

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }

  public static void loadLanguage(String baseName, String isoCode) {
    AbstractLauncherLanguageImpl.INSTANCE.loadLanguage(baseName, isoCode);
  }

  public enum ELanguage {
    EN("English");

    @Getter
    private final String name;

    ELanguage(String name) {
      this.name = name;
    }

    public static ELanguage getLanguage(String isoCode) {
      return Arrays.stream(ELanguage.values())
          .filter(language -> language.name().equalsIgnoreCase(isoCode))
          .findFirst()
          .orElse(null);
    }
  }
}
