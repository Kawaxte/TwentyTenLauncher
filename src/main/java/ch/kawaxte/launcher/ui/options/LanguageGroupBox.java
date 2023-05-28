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
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;

/**
 * Class representing a C#-esque group box for selecting the language of the launcher.
 *
 * @author Kawaxte
 * @since 1.4.0923_02
 * @see JGroupBox
 */
public class LanguageGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  private static LanguageGroupBox instance;
  private final JLabel setLanguageLabel;
  @Getter private final JComboBox<String> languageComboBox;

  /**
   * Constructor for LanguageGroupBox.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it and sets the component texts according to the currently
   * selected language.
   *
   * @see #setLayout(LayoutManager)
   * @see #updateComponentTexts(UTF8ResourceBundle)
   */
  public LanguageGroupBox() {
    super(LauncherLanguageUtils.getLGBKeys()[0], true);

    setInstance(this);

    this.setLanguageLabel = new JLabel(LauncherLanguageUtils.getLGBKeys()[1], SwingConstants.RIGHT);
    this.languageComboBox = new JComboBox<>();

    this.setLayout(this.getGroupLayout());

    this.languageComboBox.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentTexts(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());

    LauncherOptionsUtils.updateLanguageComboBox(this);
  }

  public static LanguageGroupBox getInstance() {
    return instance;
  }

  private static void setInstance(LanguageGroupBox lgb) {
    instance = lgb;
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
        bundle, this.setLanguageLabel, LauncherLanguageUtils.getLGBKeys()[1]);
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
            .addComponent(this.setLanguageLabel)
            .addComponent(this.languageComboBox));
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.setLanguageLabel)
                    .addComponent(this.languageComboBox)));
    return gl;
  }

  /**
   * Handles the actions performed on {@link #languageComboBox}.
   *
   * <p>It enables the {@code saveOptionsButton} component if the selected language is different
   * from the current one.
   *
   * @param event the action event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (Objects.equals(source, this.languageComboBox)) {
      boolean selectedLanguageEqual =
          Objects.equals(LauncherConfig.get(0), this.languageComboBox.getSelectedItem());
      OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(!selectedLanguageEqual);
    }
  }
}
