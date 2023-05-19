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
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.Window;
import java.util.Objects;
import javax.swing.JDialog;
import lombok.val;

public class OptionsDialog extends JDialog {

  public static final long serialVersionUID = 1L;
  public static OptionsDialog instance;

  public OptionsDialog(Window owner) {
    super(owner, ModalityType.MODELESS);

    OptionsDialog.instance = this;
    this.setContentPane(new OptionsPanel());

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.pack();

    this.setLocation(this.getOwner().getLocation());
    this.setResizable(false);

    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateContainerKeyValues(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.bundle);
  }

  public void updateContainerKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateContainerKeyValue(bundle, this, "od.title");
  }

  @Override
  public String getTitle() {
    return "od.title";
  }
}
