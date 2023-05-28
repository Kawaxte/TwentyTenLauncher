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

import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.impl.swing.JGroupBox;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherOptionsUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;

/**
 * Class representing a C#-esque group box for selecting various versions of Minecraft to launch.
 *
 * @author Kawaxte
 * @since 1.4.0923_02
 * @see JGroupBox
 */
public class VersionGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  private static VersionGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JLabel useVersionLabel;
  @Getter private final JComboBox<String> versionComboBox;

  /**
   * Constructor for LanguageGroupBox.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it and sets the component texts according to the currently
   * selected language.
   *
   * <p>It also sets the selected item of the {@link javax.swing.JComboBox} to the currently
   * selected version.
   *
   * @see #setLayout(LayoutManager)
   * @see #updateComponentTexts(UTF8ResourceBundle)
   */
  public VersionGroupBox() {
    super(LauncherLanguageUtils.getVGBKeys()[0], true);

    setInstance(this);

    this.showBetaVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
    this.showAlphaVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
    this.showInfdevVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
    this.useVersionLabel = new JLabel(LauncherLanguageUtils.getVGBKeys()[2], SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();

    this.setLayout(this.getGroupLayout());

    this.showBetaVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.get(1).toString()));
    this.showAlphaVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.get(2).toString()));
    this.showInfdevVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.get(3).toString()));

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);
    this.versionComboBox.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentTexts(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());

    LauncherOptionsUtils.updateVersionComboBox(this);
  }

  public static VersionGroupBox getInstance() {
    return instance;
  }

  public static void setInstance(VersionGroupBox vgb) {
    VersionGroupBox.instance = vgb;
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
    List<String> versions = Collections.unmodifiableList(Arrays.asList("Beta", "Alpha", "Infdev"));
    List<String> releaseDateRange =
        Collections.unmodifiableList(
            Arrays.asList(
                "2010-12-20 -> 2011-09-15",
                "2010-06-30 -> 2010-12-03",
                "2010-02-27 -> 2010-06-30"));

    LauncherUtils.setComponentText(
        bundle,
        this.showBetaVersionsCheckBox,
        LauncherLanguageUtils.getVGBKeys()[1],
        versions.get(0),
        releaseDateRange.get(0));
    LauncherUtils.setComponentText(
        bundle,
        this.showAlphaVersionsCheckBox,
        LauncherLanguageUtils.getVGBKeys()[1],
        versions.get(1),
        releaseDateRange.get(1));
    LauncherUtils.setComponentText(
        bundle,
        this.showInfdevVersionsCheckBox,
        LauncherLanguageUtils.getVGBKeys()[1],
        versions.get(2),
        releaseDateRange.get(2));
    LauncherUtils.setComponentText(
        bundle, this.useVersionLabel, LauncherLanguageUtils.getVGBKeys()[2]);
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
                gl.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.showBetaVersionsCheckBox)
                    .addComponent(this.showAlphaVersionsCheckBox)
                    .addComponent(this.showInfdevVersionsCheckBox)
                    .addGroup(
                        gl.createSequentialGroup()
                            .addComponent(this.useVersionLabel)
                            .addComponent(this.versionComboBox))));
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.showBetaVersionsCheckBox)
            .addComponent(this.showAlphaVersionsCheckBox)
            .addComponent(this.showInfdevVersionsCheckBox)
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.useVersionLabel)
                    .addComponent(this.versionComboBox)));
    return gl;
  }

  /**
   * Handles the actions performed on {@link #showBetaVersionsCheckBox}, {@link
   * #showAlphaVersionsCheckBox}, {@link #showInfdevVersionsCheckBox} and {@link #versionComboBox}.
   *
   * <p>It sets the status of {@code saveOptionsButton} based on which of the configuration values
   * for the version checkboxes match with the currently selected {@link javax.swing.JComboBox}, and
   * does the same on whether or not the selected version item in {@link #versionComboBox} is the
   * same as the selected version in configuration file.
   *
   * @param event the action event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();

    JCheckBox[] showVersionCheckBoxes =
        new JCheckBox[] {
          this.showBetaVersionsCheckBox,
          this.showAlphaVersionsCheckBox,
          this.showInfdevVersionsCheckBox
        };
    for (JCheckBox checkBox : showVersionCheckBoxes) {
      if (Objects.equals(source, checkBox)) {
        LauncherConfig.set(1, this.showBetaVersionsCheckBox.isSelected());
        LauncherConfig.set(2, this.showAlphaVersionsCheckBox.isSelected());
        LauncherConfig.set(3, this.showInfdevVersionsCheckBox.isSelected());

        boolean showVersionsSelected =
            Stream.of(
                    Boolean.parseBoolean(LauncherConfig.get(1).toString()),
                    Boolean.parseBoolean(LauncherConfig.get(2).toString()),
                    Boolean.parseBoolean(LauncherConfig.get(3).toString()))
                .anyMatch(Boolean::booleanValue);
        OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(showVersionsSelected);

        LauncherOptionsUtils.updateVersionComboBox(this);
        break;
      }
    }
    if (Objects.equals(source, this.versionComboBox)) {
      Object selectedVersion = LauncherConfig.get(4);
      Object selectedItem = this.versionComboBox.getSelectedItem();
      boolean selectedVersionEqual = Objects.equals(selectedVersion, selectedItem);

      OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(!selectedVersionEqual);
    }
  }
}
