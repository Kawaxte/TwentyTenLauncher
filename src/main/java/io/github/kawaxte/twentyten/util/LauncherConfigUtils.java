package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.conf.LauncherConfig;
import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle.UTF8Control;
import io.github.kawaxte.twentyten.ui.YggdrasilLoginPanel;
import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import java.util.Locale;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;
import lombok.var;

public final class LauncherConfigUtils {

  private LauncherConfigUtils() {
  }

  public static void updateLanguage(LanguageGroupBox lgb) {
    var selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = LauncherLanguage.languageLookup.get(selectedItem);

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      AbstractLauncherConfigImpl.INSTANCE.setSelectedLanguage(selectedLanguage);

      SwingUtilities.invokeLater(() -> {
        val newUtf8Bundle = (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages",
            Locale.forLanguageTag(selectedLanguage),
            new UTF8Control());
        YggdrasilLoginPanel.INSTANCE.updateComponentKeyValues(newUtf8Bundle);
      });

      LauncherConfig.saveConfig();
    }
  }
}
