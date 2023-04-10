package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class OptionsDialog extends JDialog {

  public static final long serialVersionUID = 1L;

  public OptionsDialog(Window owner) {
    super((JFrame) owner, true);

    this.setContentPane(new OptionsPanel());

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    this.pack();

    this.setLocation(this.getOwner().getLocation());
    this.setResizable(false);

    this.updateContainerKeyValues();
  }

  private void updateContainerKeyValues() {
    LauncherUtils.updateContainerKeyValue(LauncherUtils.getUtf8Bundle(),
        this,
        this.getTitle());
  }

  @Override
  public String getTitle() {
    return "od.title";
  }
}
