package ee.twentyten.ui.options;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.OptionsHelper;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;

@Getter
public class OptionsVersionPanel extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final String showVersionsCheckboxText;
  private final String useVersionLabelText;
  private final String installedText;
  private JCheckBox showBetaVersionsCheckBox;
  private JCheckBox showAlphaVersionsCheckBox;
  private JCheckBox showInfdevVersionsCheckBox;
  private JLabel useVersionLabel;
  private JComboBox<String> useVersionComboBox;

  {
    this.showVersionsCheckboxText = LauncherLanguage.getString(
        "ovp.checkbox.showVersionsCheckBox");
    this.useVersionLabelText = LauncherLanguage.getString(
        "ovp.label.useVersionLabel");
    this.installedText = LauncherLanguage.getString("od.string.installed");
  }

  public OptionsVersionPanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.createMiddlePanel();
    this.createBottomPanel();

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);
  }

  private String getFormattedVersionId(String type, String id) {
    String formattedId = null;
    if (type.equals("beta") || type.equals("alpha")) {
      formattedId = String.format(OptionsHelper.formattedVersionIds.get(type),
          id.substring(1));
    }
    if (type.equals("infdev")) {
      formattedId = String.format(OptionsHelper.formattedVersionIds.get(type),
          id.substring(3));
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
            formattedId = String.format("%s %s", formattedId,
                this.installedText);
          }
        }
      }
    }
    return formattedId;
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new GridLayout(0, 1), true);
    this.add(middlePanel, SwingConstants.CENTER);

    String showBetaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Beta", "2010-12-20 -> 2011-01-21");
    this.showBetaVersionsCheckBox = new JCheckBox(showBetaVersionsCheckboxText,
        LauncherConfig.instance.getUsingBeta());
    middlePanel.add(this.showBetaVersionsCheckBox, 0);

    String showAlphaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Alpha", "2010-07-02 -> 2010-12-03");
    this.showAlphaVersionsCheckBox = new JCheckBox(
        showAlphaVersionsCheckboxText, LauncherConfig.instance.getUsingAlpha());
    middlePanel.add(this.showAlphaVersionsCheckBox, 1);

    String showInfdevVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Infdev", "2010-06-29 -> 2010-06-30");
    this.showInfdevVersionsCheckBox = new JCheckBox(
        showInfdevVersionsCheckboxText,
        LauncherConfig.instance.getUsingInfdev());
    middlePanel.add(this.showInfdevVersionsCheckBox, 2);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.useVersionLabel = new JLabel(this.useVersionLabelText,
        SwingConstants.RIGHT);
    bottomPanel.add(this.useVersionLabel, BorderLayout.WEST);

    this.useVersionComboBox = new JComboBox<>();
    bottomPanel.add(this.useVersionComboBox, BorderLayout.CENTER);
  }

  public void updateUseVersionList() {
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
    this.useVersionComboBox.setModel(model);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.showBetaVersionsCheckBox) {
      LauncherConfig.instance.setUsingBeta(
          this.showBetaVersionsCheckBox.isSelected());
    }
    if (src == this.showAlphaVersionsCheckBox) {
      LauncherConfig.instance.setUsingAlpha(
          this.showAlphaVersionsCheckBox.isSelected());
    }
    if (src == this.showInfdevVersionsCheckBox) {
      LauncherConfig.instance.setUsingInfdev(
          this.showInfdevVersionsCheckBox.isSelected());
    }
    if (src == this.showBetaVersionsCheckBox
        || src == this.showAlphaVersionsCheckBox
        || src == this.showInfdevVersionsCheckBox) {
      this.updateUseVersionList();
    }
  }
}
