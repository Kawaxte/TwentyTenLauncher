package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.conf.LauncherConfig;
import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.ui.MicrosoftLoginPanel;
import io.github.kawaxte.twentyten.ui.YggdrasilLoginPanel;
import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.ui.options.OptionsPanel;
import io.github.kawaxte.twentyten.ui.options.VersionGroupBox;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;

public final class LauncherConfigUtils {

  private LauncherConfigUtils() {}

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = LauncherLanguage.languageLookup.get(selectedItem);

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      AbstractLauncherConfigImpl.INSTANCE.setSelectedLanguage(selectedItem);

      val finalSelectedItem = selectedItem;
      SwingUtilities.invokeLater(
          () -> {
            val newUtf8Bundle = LauncherLanguage.getUtf8Bundle(finalSelectedItem);
            if (newUtf8Bundle != null) {
              if (LanguageGroupBox.instance != null) {
                LanguageGroupBox.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (OptionsDialog.instance != null) {
                OptionsDialog.instance.updateContainerKeyValues(newUtf8Bundle);
              }
              if (OptionsPanel.instance != null) {
                OptionsPanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (VersionGroupBox.instance != null) {
                VersionGroupBox.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (LauncherOfflinePanel.instance != null) {
                LauncherOfflinePanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (MicrosoftLoginPanel.instance != null) {
                MicrosoftLoginPanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (YggdrasilLoginPanel.instance != null) {
                YggdrasilLoginPanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
            }
          });

      LauncherConfig.saveConfig();
    }
  }
}
