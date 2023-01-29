package ee.twentyten.ui;

import ee.twentyten.ui.panel.OptionsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private final OptionsPanel optionsPanel;

  public OptionsDialog(LauncherFrame frame) {
    super(frame, "Launcher Options", true);

    this.optionsPanel = new OptionsPanel();
    this.setContentPane(this.optionsPanel);

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
  }
}
