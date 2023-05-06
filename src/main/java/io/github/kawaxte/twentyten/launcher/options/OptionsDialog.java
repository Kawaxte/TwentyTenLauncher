package io.github.kawaxte.twentyten.launcher.options;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
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

    val selectedLanguage = CONFIG.getSelectedLanguage();
    this.updateContainerKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : LANGUAGE.getBundle());
  }

  public void updateContainerKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateContainerKeyValue(bundle, this, "od.title");
  }

  @Override
  public String getTitle() {
    return "od.title";
  }
}
