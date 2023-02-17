package ee.twentyten.ui;

import ee.twentyten.ui.options.OptionsLanguagePanel;
import ee.twentyten.ui.options.OptionsPanel;
import ee.twentyten.ui.options.OptionsVersionPanel;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  public OptionsDialog(String title, LauncherFrame frame) {
    super(frame, title, true);

    OptionsPanel panel = new OptionsPanel();
    this.setContentPane(panel);

    OptionsVersionPanel versionPanel = panel.getVersionPanel();
    OptionsLanguagePanel languagePanel = panel.getLanguagePanel();
    versionPanel.updateUseVersionList();
    languagePanel.updateSetLanguageList();

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());
  }
}
