package ee.twentyten.ui;

import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LauncherHelper;
import ee.twentyten.util.LogHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static final Class<OptionsDialog> CLASS_REF;

  static {
    CLASS_REF = OptionsDialog.class;
  }

  private final OptionsPanel optionsPanel;
  private final OptionsVersionsPanel optionsVersionsPanel;

  public OptionsDialog(LauncherFrame frame) {
    super(frame, "Launcher Options", true);

    this.optionsPanel = new OptionsPanel();
    this.optionsPanel.getCancelButton().addActionListener(this);
    this.optionsPanel.getOpenGameDirectoryButton().addActionListener(this);
    this.optionsPanel.getSaveOptionsButton().addActionListener(this);
    this.setContentPane(this.optionsPanel);

    this.optionsVersionsPanel = this.optionsPanel.getOptionsVersionsPanel();
    this.optionsVersionsPanel.getShowBetaVersionsCheckBox().addActionListener(this);
    this.optionsVersionsPanel.getShowAlphaVersionsCheckBox().addActionListener(this);
    this.optionsVersionsPanel.getShowInfdevVersionsCheckBox().addActionListener(this);

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());

    this.updateUseVersionList();
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
            formattedId = String.format("%s (installed)", formattedId);
            break;
          }
        }
        break;
      }
    }
    return formattedId;
  }

  private void updateUseVersionList() {
    OptionsHelper.versionIds = new HashMap<>();

    String formattedVersionId;
    String versionId;
    List<String> versionIds;

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (String type : OptionsHelper.versionTypes) {
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
    this.optionsVersionsPanel.getVersionComboBox().setModel(model);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.optionsVersionsPanel.getShowBetaVersionsCheckBox()) {
      LauncherConfig.instance.setUsingBeta(
          this.optionsVersionsPanel.getShowBetaVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionsPanel.getShowAlphaVersionsCheckBox()) {
      LauncherConfig.instance.setUsingAlpha(
          this.optionsVersionsPanel.getShowAlphaVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionsPanel.getShowInfdevVersionsCheckBox()) {
      LauncherConfig.instance.setUsingInfdev(
          this.optionsVersionsPanel.getShowInfdevVersionsCheckBox().isSelected());
    }
    if (source == this.optionsVersionsPanel.getShowBetaVersionsCheckBox()
        || source == this.optionsVersionsPanel.getShowAlphaVersionsCheckBox()
        || source == this.optionsVersionsPanel.getShowInfdevVersionsCheckBox()) {
      this.updateUseVersionList();
    }
    if (source == this.optionsPanel.getCancelButton()) {
      this.dispose();
    }
    if (source == this.optionsPanel.getOpenGameDirectoryButton()) {
      try {
        Desktop.getDesktop().open(LauncherHelper.getWorkingDirectory());
      } catch (IOException ioe1) {
        LogHelper.logError(CLASS_REF, "Failed to open working directory", ioe1);

        EPlatform platform = EPlatform.getPlatform();
        try {
          RuntimeHelper.executeCommand(platform, LauncherHelper.getWorkingDirectory());
        } catch (IOException ioe2) {
          LogHelper.logError(CLASS_REF, "Failed to execute string command", ioe2);
        }
      }
    }
    if (source == this.optionsPanel.getSaveOptionsButton()) {
      LauncherConfig.instance.save();

      this.dispose();
    }

    String selectedFormattedId = (String) this.optionsVersionsPanel.getVersionComboBox()
        .getSelectedItem();
    if (selectedFormattedId != null) {
      LauncherConfig.instance.setSelectedVersion(
          OptionsHelper.versionIds.get(selectedFormattedId));
    }
  }
}
