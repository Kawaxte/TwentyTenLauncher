package io.github.kawaxte.twentyten.launcher.ui.options;

import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.workingDirectoryPath;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.util.LauncherOptionsUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.val;

public class OptionsPanel extends JPanel implements ActionListener {

  public static final long serialVersionUID = 1L;
  public static OptionsPanel instance;
  private final LanguageGroupBox languageGroupBox;
  private final VersionGroupBox versionGroupBox;
  private final JLabel buildTimeLabel;
  private final JButton openFolderButton;
  @Getter private final JButton saveOptionsButton;

  {
    this.languageGroupBox = new LanguageGroupBox();
    this.versionGroupBox = new VersionGroupBox();
    this.buildTimeLabel =
        new JLabel(LauncherUtils.getManifestAttribute("Build-Time"), SwingUtilities.CENTER);
    this.openFolderButton = new JButton("op.openFolderButton");
    this.saveOptionsButton = new JButton("op.saveOptionsButton");
  }

  public OptionsPanel() {
    super(true);

    OptionsPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    this.buildTimeLabel.setEnabled(false);
    this.saveOptionsButton.setEnabled(false);

    this.openFolderButton.addActionListener(this);
    this.saveOptionsButton.addActionListener(this);

    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguage.getUTF8Bundle(selectedLanguage)
            : LauncherLanguage.bundle);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.languageGroupBox,
        this.languageGroupBox.setTitledBorder("op.languageGroupBox"));
    LauncherUtils.updateComponentKeyValue(
        bundle, this.versionGroupBox, this.versionGroupBox.setTitledBorder("op.versionGroupBox"));
    LauncherUtils.updateComponentKeyValue(bundle, this.openFolderButton, "op.openFolderButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.saveOptionsButton, "op.saveOptionsButton");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                    .createParallelGroup()
                    .addComponent(this.languageGroupBox)
                    .addComponent(this.versionGroupBox)
                    .addGroup(
                        groupLayout
                            .createSequentialGroup()
                            .addComponent(
                                this.buildTimeLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(
                                this.openFolderButton,
                                0,
                                GroupLayout.PREFERRED_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                this.saveOptionsButton,
                                0,
                                GroupLayout.PREFERRED_SIZE,
                                Short.MAX_VALUE))));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.languageGroupBox)
            .addComponent(this.versionGroupBox)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.CENTER)
                    .addComponent(this.buildTimeLabel)
                    .addComponent(this.openFolderButton)
                    .addComponent(this.saveOptionsButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.openFolderButton)) {
      LauncherUtils.openDesktop(workingDirectoryPath);
    }
    if (Objects.equals(source, this.saveOptionsButton)) {
      LauncherOptionsUtils.updateSelectedLanguage(this.languageGroupBox);
      LauncherOptionsUtils.updateSelectedVersion(this.versionGroupBox);

      this.saveOptionsButton.setEnabled(false);
    }
  }
}
