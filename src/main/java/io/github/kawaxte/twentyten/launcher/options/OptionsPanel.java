package io.github.kawaxte.twentyten.launcher.options;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;
import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.WORKING_DIRECTORY_PATH;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.util.JarUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
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
import lombok.val;

public class OptionsPanel extends JPanel implements ActionListener {

  public static final long serialVersionUID = 1L;
  public static OptionsPanel instance;
  private final LanguageGroupBox languageGroupBox;
  private final VersionGroupBox versionGroupBox;
  private final JLabel buildTimeLabel;
  private final JButton openDirectoryButton;
  private final JButton saveOptionsButton;

  {
    this.languageGroupBox = new LanguageGroupBox();
    this.versionGroupBox = new VersionGroupBox();
    this.buildTimeLabel =
        new JLabel(JarUtils.getManifestAttribute("Build-Time"), SwingUtilities.CENTER);
    this.openDirectoryButton = new JButton("op.openDirectoryButton");
    this.saveOptionsButton = new JButton("op.saveOptionsButton");
  }

  public OptionsPanel() {
    super(true);

    OptionsPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    this.buildTimeLabel.setEnabled(false);
    this.saveOptionsButton.setEnabled(false);

    this.openDirectoryButton.addActionListener(this);
    this.saveOptionsButton.addActionListener(this);

    val selectedLanguage = CONFIG.getSelectedLanguage();
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : LANGUAGE.getBundle());
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.languageGroupBox,
        this.languageGroupBox.setTitledBorder("op.languageGroupBox"));
    LauncherUtils.updateComponentKeyValue(
        bundle, this.versionGroupBox, this.versionGroupBox.setTitledBorder("op.versionGroupBox"));
    LauncherUtils.updateComponentKeyValue(
        bundle, this.openDirectoryButton, "op.openDirectoryButton");
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
                            .addComponent(this.buildTimeLabel, 0, 0, Short.MAX_VALUE)
                            .addComponent(this.openDirectoryButton, 0, 0, Short.MAX_VALUE)
                            .addComponent(this.saveOptionsButton, 0, 0, Short.MAX_VALUE))));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.languageGroupBox)
            .addComponent(this.versionGroupBox)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.CENTER)
                    .addComponent(this.buildTimeLabel)
                    .addComponent(this.openDirectoryButton)
                    .addComponent(this.saveOptionsButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.openDirectoryButton)) {
      LauncherUtils.openDesktop(WORKING_DIRECTORY_PATH);
    }
    if (Objects.equals(source, this.saveOptionsButton)) {
      LauncherConfigUtils.updateSelectedLanguage(this.languageGroupBox);
      LauncherConfigUtils.updateSelectedVersion(this.versionGroupBox);

      SwingUtilities.getWindowAncestor(this).dispose();
    }
  }
}
