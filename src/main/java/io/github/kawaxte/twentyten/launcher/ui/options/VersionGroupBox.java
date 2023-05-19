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
package io.github.kawaxte.twentyten.launcher.ui.options;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.custom.JGroupBox;
import io.github.kawaxte.twentyten.launcher.util.LauncherOptionsUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class VersionGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  public static VersionGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JLabel useVersionLabel;
  @Getter private final JComboBox<String> versionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showAlphaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showInfdevVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.useVersionLabel = new JLabel("vgb.useVersionLabel", SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
  }

  public VersionGroupBox() {
    super("vgb.title", true);

    VersionGroupBox.instance = this;
    this.setLayout(this.getGroupLayout());

    this.showBetaVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.lookup.get("showBetaVersionsSelected").toString()));
    this.showAlphaVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.lookup.get("showAlphaVersionsSelected").toString()));
    this.showInfdevVersionsCheckBox.setSelected(
        Boolean.parseBoolean(LauncherConfig.lookup.get("showInfdevVersionsSelected").toString()));

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);
    this.versionComboBox.addActionListener(this);

    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.bundle);

    LauncherOptionsUtils.updateVersionComboBox(this);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    val versions = Collections.unmodifiableList(Arrays.asList("Beta", "Alpha", "Infdev"));
    val releaseDateRange =
        Collections.unmodifiableList(
            Arrays.asList(
                "2010-12-20 -> 2011-09-15",
                "2010-06-30 -> 2010-12-03",
                "2010-02-27 -> 2010-06-30"));

    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showBetaVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versions.get(0),
        releaseDateRange.get(0));
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showAlphaVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versions.get(1),
        releaseDateRange.get(1));
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showInfdevVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versions.get(2),
        releaseDateRange.get(2));
    LauncherUtils.updateComponentKeyValue(bundle, this.useVersionLabel, "vgb.useVersionLabel");
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
                    .createParallelGroup(Alignment.LEADING)
                    .addComponent(this.showBetaVersionsCheckBox)
                    .addComponent(this.showAlphaVersionsCheckBox)
                    .addComponent(this.showInfdevVersionsCheckBox)
                    .addGroup(
                        groupLayout
                            .createSequentialGroup()
                            .addComponent(this.useVersionLabel)
                            .addComponent(this.versionComboBox))));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.showBetaVersionsCheckBox)
            .addComponent(this.showAlphaVersionsCheckBox)
            .addComponent(this.showInfdevVersionsCheckBox)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.useVersionLabel)
                    .addComponent(this.versionComboBox)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();

    val showVersionCheckBoxes =
        new JCheckBox[] {
          this.showBetaVersionsCheckBox,
          this.showAlphaVersionsCheckBox,
          this.showInfdevVersionsCheckBox
        };
    for (val showVersionCheckBox : showVersionCheckBoxes) {
      if (Objects.equals(source, showVersionCheckBox)) {
        LauncherConfig.lookup.put(
            "showBetaVersionsSelected", this.showBetaVersionsCheckBox.isSelected());
        LauncherConfig.lookup.put(
            "showAlphaVersionsSelected", this.showAlphaVersionsCheckBox.isSelected());
        LauncherConfig.lookup.put(
            "showInfdevVersionsSelected", this.showInfdevVersionsCheckBox.isSelected());

        val showVersionsSelected =
            Stream.of(
                    "showBetaVersionsSelected",
                    "showAlphaVersionsSelected",
                    "showInfdevVersionsSelected")
                .anyMatch(s -> (boolean) LauncherConfig.lookup.get(s));
        OptionsPanel.instance.getSaveOptionsButton().setEnabled(showVersionsSelected);

        LauncherOptionsUtils.updateVersionComboBox(this);
        break;
      }
    }
    if (Objects.equals(source, this.versionComboBox)) {
      val selectedVersionEqual =
          Objects.equals(
              LauncherConfig.lookup.get("selectedVersion"), this.versionComboBox.getSelectedItem());
      OptionsPanel.instance.getSaveOptionsButton().setEnabled(!selectedVersionEqual);
    }
  }
}
