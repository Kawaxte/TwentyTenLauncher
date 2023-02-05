package ee.twentyten.launcher.ui;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.CommandManager;
import ee.twentyten.util.LauncherManager;
import ee.twentyten.util.LoggingManager;
import ee.twentyten.util.OptionsManager;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;
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
    this.optionsVersionsPanel.showBetaVersionsCheckBox.addActionListener(this);
    this.optionsVersionsPanel.showAlphaVersionsCheckBox.addActionListener(this);
    this.optionsVersionsPanel.showInfdevVersionsCheckBox.addActionListener(this);

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());
  }

  private void updateUseVersionList() {
    List<String> ids;
    String id;
    String formattedId;
    OptionsManager.versionIds = new HashMap<>();

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (int versionIndex = OptionsManager.versionTypes.length - 1; versionIndex >= 0;
        versionIndex--) {
      String type = OptionsManager.versionTypes[versionIndex];
      if (Config.instance.getUsingBeta() && type.equals("beta")) {
        ids = OptionsManager.getVersionIds(type);
        for (int idIndex = ids.size() - 1; idIndex >= 0; idIndex--) {
          id = ids.get(idIndex);
          formattedId = String.format(OptionsManager.formattedVersionIds.get(type),
              id.substring(1));
          model.addElement(formattedId);

          OptionsManager.versionIds.put(formattedId, id);
        }
      }
      if (Config.instance.getUsingAlpha() && type.equals("alpha")) {
        ids = OptionsManager.getVersionIds(type);
        for (int idIndex = ids.size() - 1; idIndex >= 0; idIndex--) {
          id = ids.get(idIndex);
          formattedId = String.format(OptionsManager.formattedVersionIds.get(type),
              id.substring(1));
          model.addElement(formattedId);

          OptionsManager.versionIds.put(formattedId, id);
        }
      }
      if (Config.instance.getUsingInfdev() && type.equals("infdev")) {
        ids = OptionsManager.getVersionIds(type);
        for (int idIndex = ids.size() - 1; idIndex >= 0; idIndex--) {
          id = ids.get(idIndex);
          formattedId = String.format(OptionsManager.formattedVersionIds.get(type),
              id.substring(3));
          model.addElement(formattedId);

          OptionsManager.versionIds.put(formattedId, id);
        }
      }
    }

    String selectedVersion = Config.instance.getSelectedVersion();
    for (Map.Entry<String, String> entry : OptionsManager.versionIds.entrySet()) {
      if (entry.getValue().equals(selectedVersion)) {
        model.setSelectedItem(entry.getKey());
        break;
      }
    }
    this.optionsVersionsPanel.versionComboBox.setModel(model);
  }

  @Override
  public void actionPerformed(ActionEvent event) {

    Object source = event.getSource();
    if (source == this.optionsVersionsPanel.showBetaVersionsCheckBox) {
      Config.instance.setUsingBeta(this.optionsVersionsPanel.showBetaVersionsCheckBox.isSelected());
    }
    if (source == this.optionsVersionsPanel.showAlphaVersionsCheckBox) {
      Config.instance.setUsingAlpha(
          this.optionsVersionsPanel.showAlphaVersionsCheckBox.isSelected());
    }
    if (source == this.optionsVersionsPanel.showInfdevVersionsCheckBox) {
      Config.instance.setUsingInfdev(
          this.optionsVersionsPanel.showInfdevVersionsCheckBox.isSelected());
    }
    String selectedFormattedId = (String) this.optionsVersionsPanel.versionComboBox.getSelectedItem();
    if (selectedFormattedId != null) {
      Config.instance.setSelectedVersion(OptionsManager.versionIds.get(selectedFormattedId));
    }

    if (source == this.optionsPanel.getCancelButton()) {
      this.dispose();
    }
    if (source == this.optionsPanel.getOpenGameDirectoryButton()) {
      try {
        Desktop.getDesktop().open(LauncherManager.getWorkingDirectory());
      } catch (IOException ioe1) {
        LoggingManager.logError(this.getClass(), "Failed to open working directory", ioe1);

        EPlatform platform = EPlatform.getPlatform();
        try {
          CommandManager.executeCommand(platform, LauncherManager.getWorkingDirectory());
        } catch (IOException ioe2) {
          LoggingManager.logError(this.getClass(), "Failed to execute string command", ioe2);
        }
      }
    }
    if (source == this.optionsPanel.getSaveOptionsButton()) {
      Config.instance.save();
      this.dispose();
    }

    this.updateUseVersionList();
  }
}
