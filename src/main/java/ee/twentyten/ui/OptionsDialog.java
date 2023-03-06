package ee.twentyten.ui;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.ui.options.OptionsPanel;
import ee.twentyten.util.LanguageUtils;
import javax.swing.JDialog;
import javax.swing.JFrame;
import lombok.Getter;
import lombok.Setter;

public class OptionsDialog extends JDialog {

  @Getter
  @Setter
  private static OptionsDialog instance;

  public OptionsDialog(JFrame owner, boolean modal) {
    super(owner, LanguageUtils.getString("od.string.title"), modal);

    OptionsDialog.setInstance(this);
    this.add(new OptionsPanel());
    this.pack();

    this.setLocation(this.getOwner().getLocation());
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setResizable(false);
    this.setVisible(true);

    this.setTextToContainers(LanguageUtils.getBundle());
  }

  public void setTextToContainers(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToContainer(bundle, this, "od.string.title");
  }
}
