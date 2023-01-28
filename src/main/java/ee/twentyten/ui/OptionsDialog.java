package ee.twentyten.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private final OptionsTabbedPane optionsTabbedPane;
  private final OptionsPanel optionsPanel;

  public OptionsDialog(LauncherFrame frame) {
    super(frame, "Launcher Options", true);

    this.optionsPanel = new OptionsPanel();
    this.optionsTabbedPane = new OptionsTabbedPane();
    this.optionsPanel.add(this.optionsTabbedPane);
    this.setContentPane(this.optionsPanel);

    this.pack();

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    /*
    VersionsOptionsPanel versionsOptionsPanel = this.optionsTabbedPane.getVersionsOptionsPanel();

    Object source = ae.getSource();
    if (source == versionsOptionsPanel.getShowBetaVersionsCheckBox()) {
      // TODO: display only the beta versions from the "versions.json" file.
    }
    if (source == versionsOptionsPanel.getShowAlphaVersionsCheckBox()) {
      // TODO: display only the alpha versions from the "versions.json" file.
    }
    if (source == versionsOptionsPanel.getShowInfdevVersionsCheckBox()) {
      // TODO: display only the infdev versions from the "versions.json" file.
    }
     */
  }
}
