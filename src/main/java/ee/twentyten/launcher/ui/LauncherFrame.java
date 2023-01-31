package ee.twentyten.launcher.ui;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.CommandsManager;
import ee.twentyten.util.ConfigManager;
import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.FilesManager;
import ee.twentyten.util.LauncherManager;
import ee.twentyten.util.OptionsManager;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class LauncherFrame extends JFrame implements ActionListener {

  private static final int PRE_RELEASE_VERSION;
  private static final String CURRENT_VERSION;
  public static LauncherFrame instance;

  static {
    PRE_RELEASE_VERSION = 1;
    CURRENT_VERSION = LauncherManager.getCurrentVersion(PRE_RELEASE_VERSION, true);
  }

  private final LauncherLoginPanel loginPanel;

  public LauncherFrame(String title) {
    super(title);

    this.setIconImage(FilesManager.readImageFile(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    LauncherPanel panel = new LauncherPanel();
    this.loginPanel = panel.getLauncherLoginPanel();
    this.loginPanel.getOptionsButton().addActionListener(this);
    this.loginPanel.getLinkLabel().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent ae) {
        try {
          Desktop.getDesktop().browse(URI.create(loginPanel.getLinkUrls()));
        } catch (IOException ioe1) {
          DebugLoggingManager.logError(this.getClass(), "Failed to launch default browser", ioe1);

          EPlatform platform = EPlatform.getPlatform();
          Objects.requireNonNull(platform, "platform");
          try {
            CommandsManager.executeCommand(platform, loginPanel.getLinkUrls());
          } catch (IOException ioe2) {
            DebugLoggingManager.logError(this.getClass(), "Failed to execute string command", ioe2);
          }
        }
      }
    });
    this.setContentPane(panel);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherManager.getWorkingDirectoryForPlatform();
    LauncherManager.setLookAndFeel();

    Config.instance = Config.load();
    if (Config.instance.getClientToken() == null) {
      ConfigManager.initConfig();
    }

    try {
      OptionsManager.getVersionsFile();
    } catch (IOException ioe) {
      DebugLoggingManager.logError(LauncherFrame.class, "Failed to get versions file", ioe);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.instance = new LauncherFrame(
            String.format("TwentyTen Launcher %s", CURRENT_VERSION));
        LauncherFrame.instance.setVisible(true);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == loginPanel.getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(LauncherFrame.instance);
      optionsDialog.setVisible(true);
    }
  }
}
