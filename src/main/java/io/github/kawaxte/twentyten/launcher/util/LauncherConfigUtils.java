package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.LanguageGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.ui.OptionsPanel;
import io.github.kawaxte.twentyten.launcher.ui.VersionGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;

public final class LauncherConfigUtils {

  private LauncherConfigUtils() {}

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = LauncherLanguageUtils.languageLookup.get(selectedItem);

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      LauncherConfig.lookup.put("selectedLanguage", selectedItem);
      LauncherConfig.saveConfig();

      val finalSelectedItem = selectedItem;
      SwingUtilities.invokeLater(
          () -> {
            val bundle = LauncherLanguageUtils.getUTF8Bundle(finalSelectedItem);
            if (bundle != null) {
              if (LanguageGroupBox.instance != null) {
                LanguageGroupBox.instance.updateComponentKeyValues(bundle);
              }
              if (OptionsDialog.instance != null) {
                OptionsDialog.instance.updateContainerKeyValues(bundle);
              }
              if (OptionsPanel.instance != null) {
                OptionsPanel.instance.updateComponentKeyValues(bundle);
              }
              if (VersionGroupBox.instance != null) {
                VersionGroupBox.instance.updateComponentKeyValues(bundle);
              }
              if (LauncherOfflinePanel.instance != null) {
                LauncherOfflinePanel.instance.updateComponentKeyValues(bundle);
              }
              if (MicrosoftAuthPanel.instance != null) {
                MicrosoftAuthPanel.instance.updateComponentKeyValues(bundle);
              }
              if (YggdrasilAuthPanel.instance != null) {
                YggdrasilAuthPanel.instance.updateComponentKeyValues(bundle);
              }
            }
          });
    }
  }

  public static void updateSelectedVersion(VersionGroupBox vgb) {
    String selectedItem = (String) vgb.getVersionComboBox().getSelectedItem();
    selectedItem = LauncherVersionUtils.lookup.get(selectedItem);

    val selectedVersion = LauncherConfig.lookup.get("selectedVersion");
    boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
    if (versionChanged) {
      LauncherConfig.lookup.put("selectedVersion", selectedItem);
      LauncherConfig.saveConfig();
    }
  }
}
