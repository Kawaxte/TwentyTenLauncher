package ee.twentyten.ui.options;

import ee.twentyten.EPlatform;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import lombok.Getter;

@Getter
public class OptionsPanel extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final String versionTabText;
  private final String languageTabText;
  private final String cancelButtonText;
  private final String openGameDirectoryButtonText;
  private final String saveOptionsButtonText;
  private OptionsVersionPanel versionPanel;
  private OptionsLanguagePanel languagePanel;
  private JTabbedPane tabbedPane;
  private JButton cancelButton;
  private JButton openGameDirectoryButton;
  private JButton saveOptionsButton;

  {
    this.versionTabText = LauncherLanguage.getString("ovp.string.title");
    this.languageTabText = LauncherLanguage.getString("olp.string.title");
    this.cancelButtonText = LauncherLanguage.getString(
        "op.button.cancelButton");
    this.openGameDirectoryButtonText = LauncherLanguage.getString(
        "op.button.openGameDirectoryButton");
    this.saveOptionsButtonText = LauncherLanguage.getString(
        "op.button.saveOptionsButton");
  }

  public OptionsPanel() {
    super(new BorderLayout(), true);

    this.createTabbedPane();
    this.createBottomPanel();

    this.cancelButton.addActionListener(this);
    this.openGameDirectoryButton.addActionListener(this);
    this.saveOptionsButton.addActionListener(this);
  }

  private void createTabbedPane() {
    this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    this.add(this.tabbedPane, BorderLayout.NORTH);

    this.versionPanel = new OptionsVersionPanel();
    this.tabbedPane.add(this.versionTabText, this.versionPanel);

    this.languagePanel = new OptionsLanguagePanel();
    this.tabbedPane.add(this.languageTabText, this.languagePanel);
  }

  private void createBottomPanel() {
    int tabbedPaneWidth = this.tabbedPane.getPreferredSize().width;

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.cancelButton = new JButton(this.cancelButtonText);
    bottomPanel.add(this.cancelButton);
    bottomPanel.add(Box.createHorizontalStrut(tabbedPaneWidth >> 2));

    this.openGameDirectoryButton = new JButton(
        this.openGameDirectoryButtonText);
    bottomPanel.add(this.openGameDirectoryButton);

    this.saveOptionsButton = new JButton(this.saveOptionsButtonText);
    bottomPanel.add(this.saveOptionsButton);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.cancelButton) {
      Window parent = (Window) this.getTopLevelAncestor();
      parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
    }
    if (src == this.openGameDirectoryButton) {
      try {
        Desktop.getDesktop().open(FileHelper.workingDirectory);
      } catch (IOException ioe1) {
        LoggerHelper.logError("Failed to open working directory", ioe1, true);

        EPlatform platform = EPlatform.getPlatform();
        try {
          RuntimeHelper.executeCommand(platform, FileHelper.workingDirectory);
        } catch (IOException ioe2) {
          LoggerHelper.logError("Failed to execute string command", ioe2, true);
        }
      }
    }
    if (src == this.saveOptionsButton) {
      String selectedVersion = (String) this.versionPanel.getUseVersionComboBox()
          .getSelectedItem();
      String selectedLanguage = (String) this.languagePanel.getSetLanguageComboBox()
          .getSelectedItem();
      if (selectedVersion != null) {
        LauncherConfig.instance.setSelectedVersion(
            OptionsHelper.versionIds.get(selectedVersion));
      }
      if (selectedLanguage != null && !OptionsHelper.languages.get(
              selectedLanguage)
          .equals(LauncherConfig.instance.getSelectedLanguage())) {
        LauncherConfig.instance.setSelectedLanguage(
            OptionsHelper.languages.get(selectedLanguage));
        LauncherConfig.instance.saveConfig();

        LauncherLanguage.setLanguage(
            LauncherConfig.instance.getSelectedLanguage());

        LauncherFrame.instance.dispose();

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            LauncherFrame.instance = new LauncherFrame(
                LauncherFrame.launcherTitle);
            LauncherFrame.instance.setVisible(true);
          }
        });
        return;
      }

      LauncherConfig.instance.saveConfig();
    }
  }
}
