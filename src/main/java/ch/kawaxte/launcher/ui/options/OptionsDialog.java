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
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import java.awt.Window;
import java.util.Objects;
import javax.swing.JDialog;

/**
 * Class representing a non-modal dialog window for presenting various options to the user.
 *
 * <p>Note that the language used in this dialog can be dynamically changed according to the
 * launcher's configuration.
 *
 * @see javax.swing.JDialog
 * @see OptionsPanel
 * @author Kawaxte
 * @since 1.4.0923_02
 */
public class OptionsDialog extends JDialog {

  public static final long serialVersionUID = 1L;
  private static OptionsDialog instance;

  /**
   * Constructor for OptionsDialog.
   *
   * <p>Sets up the dialog's properties, including its content pane, default close operation, size,
   * location, and resizable state. Also, sets the dialog's titles according to the currently
   * selected language.
   *
   * @param owner the {@link java.awt.Window} from which the dialog is displayed
   */
  public OptionsDialog(Window owner) {
    super(owner, ModalityType.MODELESS);

    setInstance(this);

    this.setContentPane(new OptionsPanel());

    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.pack();

    this.setLocation(this.getOwner().getLocation());
    this.setResizable(false);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateContainerTitles(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
  }

  /**
   * Returns the current instance of OptionsDialog.
   *
   * @return the instance of OptionsDialog
   */
  public static OptionsDialog getInstance() {
    return instance;
  }

  /**
   * Sets the current instance of OptionsDialog.
   *
   * @param od the instance of OptionsDialog to be set
   */
  private static void setInstance(OptionsDialog od) {
    instance = od;
  }

  /**
   * Updates the title of the dialog.
   *
   * <p>The title is set according to the provided {@link UTF8ResourceBundle}.
   *
   * @param bundle the {@link UTF8ResourceBundle} containing the localised keys and values in the
   *     resource bundle
   */
  public void updateContainerTitles(UTF8ResourceBundle bundle) {
    LauncherUtils.setContainerTitle(bundle, this, LauncherLanguageUtils.getODKeys()[0]);
  }

  /**
   * Returns the title of this dialog window.
   *
   * <p>The title is fetched from the {@link LauncherLanguageUtils} based on a predefined key.
   *
   * @return a string representing the title of this dialog window
   */
  @Override
  public String getTitle() {
    return LauncherLanguageUtils.getODKeys()[0];
  }
}
