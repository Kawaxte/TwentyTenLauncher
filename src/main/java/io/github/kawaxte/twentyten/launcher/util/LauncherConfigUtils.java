package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.launcher.AbstractLauncherLanguageImpl;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.auth.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.auth.YggdrasilAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.VersionGroupBox;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfigUtils {

  public static final AbstractLauncherConfigImpl configInstance;
  public static final AbstractLauncherLanguageImpl languageInstance;
  static Logger LOGGER;

  static {
    configInstance = new AbstractLauncherConfigImpl();
    languageInstance = new AbstractLauncherLanguageImpl();
    LOGGER = LogManager.getLogger(LauncherConfigUtils.class);
  }

  private LauncherConfigUtils() {}

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = LauncherLanguageUtils.languageLookup.get(selectedItem);

    val selectedLanguage = configInstance.getSelectedLanguage();
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      configInstance.setSelectedLanguage(selectedItem);
      configInstance.save();

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
    selectedItem = LauncherVersionUtils.versionLookup.get(selectedItem);

    val selectedVersion = configInstance.getSelectedVersion();
    boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
    if (versionChanged) {
      configInstance.setSelectedVersion(selectedItem);
      configInstance.save();
    }
  }
}
