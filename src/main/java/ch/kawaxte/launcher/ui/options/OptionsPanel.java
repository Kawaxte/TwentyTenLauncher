/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.kawaxte.launcher.ui.options;

import static ch.kawaxte.launcher.util.LauncherUtils.WORKING_DIRECTORY_PATH;

import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherOptionsUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;

/**
 * Class representing the {@link javax.swing.JPanel} that contains various interactive components
 * for the user to personalise the launcher with.
 *
 * <p>It also handles any action that the user may perform on the components.
 *
 * @author Kawaxte
 * @since 1.4.0923_02
 */
public class OptionsPanel extends JPanel implements ActionListener {

  public static final long serialVersionUID = 1L;
  private static OptionsPanel instance;
  private final LanguageGroupBox languageGroupBox;
  private final VersionGroupBox versionGroupBox;
  private final JLabel buildTimeLabel;
  private final JButton openFolderButton;
  @Getter private final JButton saveOptionsButton;

  /**
   * Constructor for OptionsPanel.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it and sets the component texts according to the currently
   * selected language.
   *
   * @see #setLayout(LayoutManager)
   * @see #updateComponentTexts(UTF8ResourceBundle)
   */
  public OptionsPanel() {
    super(true);

    setInstance(this);

    this.languageGroupBox = new LanguageGroupBox();
    this.versionGroupBox = new VersionGroupBox();
    this.buildTimeLabel =
        new JLabel(LauncherUtils.getManifestAttribute("Build-Time"), SwingConstants.CENTER);
    this.openFolderButton = new JButton(LauncherLanguageUtils.getOPKeys()[2]);
    this.saveOptionsButton = new JButton(LauncherLanguageUtils.getOPKeys()[3]);

    this.setLayout(this.getGroupLayout());

    this.buildTimeLabel.setEnabled(false);
    this.saveOptionsButton.setEnabled(false);

    this.openFolderButton.addActionListener(this);
    this.saveOptionsButton.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentTexts(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
  }

  public static OptionsPanel getInstance() {
    return instance;
  }

  private static void setInstance(OptionsPanel op) {
    instance = op;
  }

  /**
   * Updates the texts of the components.
   *
   * <p>The texts are set according to the provided {@link UTF8ResourceBundle}.
   *
   * @param bundle the {@link UTF8ResourceBundle} containing the localised keys and values in the
   *     resource bundle
   */
  public void updateComponentTexts(UTF8ResourceBundle bundle) {
    LauncherUtils.setComponentText(
        bundle,
        this.languageGroupBox,
        this.languageGroupBox.setTitledBorder(LauncherLanguageUtils.getOPKeys()[1]));
    LauncherUtils.setComponentText(
        bundle,
        this.versionGroupBox,
        this.versionGroupBox.setTitledBorder(LauncherLanguageUtils.getOPKeys()[0]));
    LauncherUtils.setComponentText(
        bundle, this.openFolderButton, LauncherLanguageUtils.getOPKeys()[2]);
    LauncherUtils.setComponentText(
        bundle, this.saveOptionsButton, LauncherLanguageUtils.getOPKeys()[3]);
  }

  /**
   * Creates and returns the {@link javax.swing.GroupLayout} used to layout the components in the
   * panel.
   *
   * @return the layout of the panel
   */
  private LayoutManager getGroupLayout() {
    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createSequentialGroup()
            .addGroup(
                gl.createParallelGroup()
                    .addComponent(this.languageGroupBox)
                    .addComponent(this.versionGroupBox)
                    .addGroup(
                        gl.createSequentialGroup()
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
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.languageGroupBox)
            .addComponent(this.versionGroupBox)
            .addGroup(
                gl.createParallelGroup(Alignment.CENTER)
                    .addComponent(this.buildTimeLabel)
                    .addComponent(this.openFolderButton)
                    .addComponent(this.saveOptionsButton)));
    return gl;
  }

  /**
   * Handles the actions performed on {@link #openFolderButton} and {@link #saveOptionsButton}.
   *
   * <p>When the {@link #openFolderButton} is clicked, the working directory of the launcher is
   * opened in the system file explorer.
   *
   * <p>When the {@link #saveOptionsButton} is clicked, the selected language and version are
   * updated, and the save button is disabled.
   *
   * @param event the action event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (Objects.equals(source, this.openFolderButton)) {
      LauncherUtils.openDesktop(WORKING_DIRECTORY_PATH);
    }
    if (Objects.equals(source, this.saveOptionsButton)) {
      LauncherOptionsUtils.updateSelectedLanguage(this.languageGroupBox);
      LauncherOptionsUtils.updateSelectedVersion(this.versionGroupBox);

      this.saveOptionsButton.setEnabled(false);
    }
  }
}
