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

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    this.updateContainerKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguage.getUTF8Bundle((String) selectedLanguage)
            : LauncherLanguage.bundle);
  }

  public void updateContainerKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateContainerKeyValue(bundle, this, "od.title");
  }

  @Override
  public String getTitle() {
    return "od.title";
  }
}
