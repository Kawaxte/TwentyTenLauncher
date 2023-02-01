package ee.twentyten.launcher.ui;

import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.CommandsManager;
import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.LauncherManager;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final OptionsPanel optionsPanel;

  public OptionsDialog(LauncherFrame frame) {
    super(frame, "Launcher Options", true);

    this.optionsPanel = new OptionsPanel();
    this.optionsPanel.getCancelButton().addActionListener(this);
    this.optionsPanel.getOpenGameDirectoryButton().addActionListener(this);
    this.optionsPanel.getSaveOptionsButton().addActionListener(this);
    this.setContentPane(this.optionsPanel);

    this.pack();
    this.setResizable(false);

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setLocation(frame.getX(), frame.getY());
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == this.optionsPanel.getCancelButton()) {
      this.dispose();
    }
    if (source == this.optionsPanel.getOpenGameDirectoryButton()) {
      try {
        Desktop.getDesktop().open(LauncherManager.getWorkingDirectory());
      } catch (IOException ioe1) {
        DebugLoggingManager.logError(this.getClass(), "Failed to open working directory", ioe1);

        EPlatform platform = EPlatform.getPlatform();
        try {
          CommandsManager.executeCommand(platform, LauncherManager.getWorkingDirectory());
        } catch (IOException ioe2) {
          DebugLoggingManager.logError(this.getClass(), "Failed to execute string command", ioe2);
        }
      }
    }
  }
}
