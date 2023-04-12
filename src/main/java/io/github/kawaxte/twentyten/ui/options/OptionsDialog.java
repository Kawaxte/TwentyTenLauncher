package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.Window;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JFrame;
import lombok.val;

public class OptionsDialog extends JDialog {

  public static final long serialVersionUID = 1L;
  public static OptionsDialog instance;

  public OptionsDialog(Window owner) {
    super((JFrame) owner, true);

    OptionsDialog.instance = this;
    this.setContentPane(new OptionsPanel());

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    this.pack();

    this.setLocation(this.getOwner().getLocation());
    this.setResizable(false);

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    this.updateContainerKeyValues(Objects.nonNull(selectedLanguage)
        ? LauncherLanguage.getUtf8Bundle(selectedLanguage)
        : LauncherLanguage.getUtf8Bundle());
  }

  public void updateContainerKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateContainerKeyValue(bundle,
        this,
        "od.title");
  }

  @Override
  public String getTitle() {
    return "od.title";
  }
}
