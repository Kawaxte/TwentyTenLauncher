package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.launcher.AbstractLauncherLanguageImpl;
import io.github.kawaxte.twentyten.launcher.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.launcher.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.options.OptionsPanel;
import io.github.kawaxte.twentyten.launcher.options.VersionGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherConfigUtils {

  public static final AbstractLauncherConfigImpl CONFIG;
  public static final AbstractLauncherLanguageImpl LANGUAGE;
  static Logger LOGGER;

  static {
    CONFIG = new AbstractLauncherConfigImpl();
    LANGUAGE = new AbstractLauncherLanguageImpl();
    LOGGER = LogManager.getLogger(LauncherConfigUtils.class);
  }

  private LauncherConfigUtils() {}

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = LauncherLanguageUtils.languageLookup.get(selectedItem);

    val selectedLanguage = CONFIG.getSelectedLanguage();
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      CONFIG.setSelectedLanguage(selectedItem);
      CONFIG.save();

      val finalSelectedItem = selectedItem;
      SwingUtilities.invokeLater(
          () -> {
            val newUtf8Bundle = LauncherLanguageUtils.getUTF8Bundle(finalSelectedItem);
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
              if (MicrosoftAuthPanel.instance != null) {
                MicrosoftAuthPanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
              if (YggdrasilAuthPanel.instance != null) {
                YggdrasilAuthPanel.instance.updateComponentKeyValues(newUtf8Bundle);
              }
            }
          });
    }
  }

  public static void updateSelectedVersion(VersionGroupBox vgb) {
    String selectedItem = (String) vgb.getVersionComboBox().getSelectedItem();
    selectedItem = LauncherVersionUtils.versionLookup.get(selectedItem);

    val selectedVersion = CONFIG.getSelectedVersion();
    boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
    if (versionChanged) {
      CONFIG.setSelectedVersion(selectedItem);
      CONFIG.save();
    }
  }
}
