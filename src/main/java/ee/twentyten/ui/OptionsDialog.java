package ee.twentyten.ui;

import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.ELanguage;
import ee.twentyten.ui.options.OptionsLanguagePanel;
import ee.twentyten.ui.options.OptionsPanel;
import ee.twentyten.ui.options.OptionsVersionPanel;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LanguageHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final OptionsPanel optionsPanel;
  private final OptionsVersionPanel optionsVersionPanel;
  private final OptionsLanguagePanel optionsLanguagePanel;
  private final String installedText;

  {
    this.installedText = LanguageHelper.getString("od.string.installed.text");
  }

  public OptionsDialog(String title, LauncherFrame frame) {
    super(frame, title, true);

    this.optionsPanel = new OptionsPanel();
    this.optionsPanel.getCancelButton().addActionListener(this);
    this.optionsPanel.getOpenGameDirectoryButton().addActionListener(this);
    this.optionsPanel.getSaveOptionsButton().addActionListener(this);
    this.setContentPane(this.optionsPanel);

    this.optionsVersionPanel = this.optionsPanel.getOptionsVersionPanel();
    this.optionsVersionPanel.getShowBetaVersionsCheckBox().addActionListener(this);
    this.optionsVersionPanel.getShowAlphaVersionsCheckBox().addActionListener(this);
    this.optionsVersionPanel.getShowInfdevVersionsCheckBox().addActionListener(this);

    this.optionsLanguagePanel = this.optionsPanel.getOptionsLanguagePanel();

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());

    this.updateUseVersionList();
    this.updateSetLanguageList();
  }

  private String getFormattedVersionId(String type, String id) {
    String formattedId = null;
    if (type.equals("beta") || type.equals("alpha")) {
      formattedId = String.format(OptionsHelper.formattedVersionIds.get(type), id.substring(1));
    }
    if (type.equals("infdev")) {
      formattedId = String.format(OptionsHelper.formattedVersionIds.get(type), id.substring(3));
    }

    File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
    File[] versionDirectories = versionsDirectory.listFiles();
    Objects.requireNonNull(versionDirectories, "versionDirectories == null!");

    for (File versionDirectory : versionDirectories) {
      if (versionDirectory.getName().equals(id)) {
        File[] versionFiles = versionDirectory.listFiles();
        Objects.requireNonNull(versionFiles, "versionFiles == null!");

        for (File versionFile : versionFiles) {
          String clientJarName = String.format("%s.jar", id);
          if (versionFile.getName().equals(clientJarName)) {
            formattedId = String.format("%s %s", formattedId, this.installedText);
          }
        }
      }
    }
    return formattedId;
  }

  private void updateUseVersionList() {
    OptionsHelper.versionIds = new HashMap<>();

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (String type : OptionsHelper.versionTypes) {
      String formattedVersionId;
      String versionId;
      List<String> versionIds;

      if (LauncherConfig.instance.getUsingBeta() && type.equals("beta")) {
        versionIds = OptionsHelper.getVersionIds(type);
        for (int idIndex = versionIds.size() - 1; idIndex >= 0; idIndex--) {
          versionId = versionIds.get(idIndex);
          formattedVersionId = this.getFormattedVersionId(type, versionId);
          model.addElement(formattedVersionId);

          OptionsHelper.versionIds.put(formattedVersionId, versionId);
        }
      }
      if (LauncherConfig.instance.getUsingAlpha() && type.equals("alpha")) {
        versionIds = OptionsHelper.getVersionIds(type);
        for (int idIndex = versionIds.size() - 1; idIndex >= 0; idIndex--) {
          versionId = versionIds.get(idIndex);
          formattedVersionId = this.getFormattedVersionId(type, versionId);
          model.addElement(formattedVersionId);

          OptionsHelper.versionIds.put(formattedVersionId, versionId);
        }
      }
      if (LauncherConfig.instance.getUsingInfdev() && type.equals("infdev")) {
        versionIds = OptionsHelper.getVersionIds(type);
        for (int idIndex = versionIds.size() - 1; idIndex >= 0; idIndex--) {
          versionId = versionIds.get(idIndex);
          formattedVersionId = this.getFormattedVersionId(type, versionId);
          model.addElement(formattedVersionId);

          OptionsHelper.versionIds.put(formattedVersionId, versionId);
        }
      }
    }

    String selectedVersion = LauncherConfig.instance.getSelectedVersion();
    for (Map.Entry<String, String> entry : OptionsHelper.versionIds.entrySet()) {
      if (entry.getValue().equals(selectedVersion)) {
        model.setSelectedItem(entry.getKey());
        break;
      }
    }
    this.optionsVersionPanel.getUseVersionComboBox().setModel(model);
  }

  private void updateSetLanguageList() {
    OptionsHelper.languages = new HashMap<>();

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (ELanguage language : ELanguage.values()) {
      String languageName = language.getName();
      String languageValue = language.toString().substring(9);
      model.addElement(languageName);

      OptionsHelper.languages.put(languageName, languageValue.toLowerCase(Locale.ROOT));
    }

    String selectedLanguage = LauncherConfig.instance.getSelectedLanguage();
    for (Map.Entry<String, String> entry : OptionsHelper.languages.entrySet()) {
      if (entry.getValue().equals(selectedLanguage)) {
        model.setSelectedItem(entry.getKey());
        break;
      }
    }
    this.optionsLanguagePanel.getSetLanguageComboBox().setModel(model);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.optionsVersionPanel.getShowBetaVersionsCheckBox()) {
      LauncherConfig.instance.setUsingBeta(
          this.optionsVersionPanel.getShowBetaVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionPanel.getShowAlphaVersionsCheckBox()) {
      LauncherConfig.instance.setUsingAlpha(
          this.optionsVersionPanel.getShowAlphaVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionPanel.getShowInfdevVersionsCheckBox()) {
      LauncherConfig.instance.setUsingInfdev(
          this.optionsVersionPanel.getShowInfdevVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionPanel.getShowBetaVersionsCheckBox()
        || source == this.optionsVersionPanel.getShowAlphaVersionsCheckBox()
        || source == this.optionsVersionPanel.getShowInfdevVersionsCheckBox()) {
      this.updateUseVersionList();
    }
    if (source == this.optionsPanel.getCancelButton()) {
      this.dispose();
    }
    if (source == this.optionsPanel.getOpenGameDirectoryButton()) {
      try {
        Desktop.getDesktop().open(FileHelper.workingDirectory);
      } catch (IOException ioe1) {
        LoggerHelper.logError("Failed to open working directory", ioe1, true);

        EPlatform platform = EPlatform.getPlatform();
        try {
          RuntimeHelper.executeFile(platform, FileHelper.workingDirectory);
        } catch (IOException ioe2) {
          LoggerHelper.logError("Failed to execute string command", ioe2, true);
        }
      }
    }
    if (source == this.optionsPanel.getSaveOptionsButton()) {
      String selectedVersion = (String) this.optionsVersionPanel.getUseVersionComboBox()
          .getSelectedItem();
      String selectedLanguage = (String) this.optionsLanguagePanel.getSetLanguageComboBox()
          .getSelectedItem();
      if (selectedVersion != null) {
        LauncherConfig.instance.setSelectedVersion(OptionsHelper.versionIds.get(selectedVersion));
      }
      if (selectedLanguage != null && !OptionsHelper.languages.get(selectedLanguage)
          .equals(LauncherConfig.instance.getSelectedLanguage())) {
        LauncherConfig.instance.setSelectedLanguage(OptionsHelper.languages.get(selectedLanguage));
        LauncherConfig.instance.saveConfig();

        LauncherFrame.instance.dispose();
      }

      LauncherConfig.instance.saveConfig();
    }
  }
}
