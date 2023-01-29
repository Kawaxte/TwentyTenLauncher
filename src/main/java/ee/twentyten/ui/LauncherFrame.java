package ee.twentyten.ui;

import ee.twentyten.LauncherConfig;
import ee.twentyten.core.event.CustomMouseListener;
import ee.twentyten.ui.panel.LauncherPanel;
import ee.twentyten.ui.panel.LoginLauncherPanel;
import ee.twentyten.util.ConfigManager;
import ee.twentyten.util.ImageManager;
import ee.twentyten.util.LauncherManager;
import ee.twentyten.util.VersionManager;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class LauncherFrame extends JFrame implements ActionListener, CustomMouseListener {

  private static final long serialVersionUID = 1L;
  private static LauncherFrame frameInstance;
  private final LauncherPanel panel;
  private final LoginLauncherPanel loginPanel;
  private MouseListener adapter;

  public LauncherFrame() {
    super(String.format("TwentyTen Launcher %s", LauncherManager.getCurrentVersion()));

    this.setIconImage(ImageManager.readImage(LauncherFrame.class, "icon/favicon.png"));
    this.setMinimumSize(new Dimension(640, 480));

    this.panel = new LauncherPanel();
    this.loginPanel = this.panel.getLoginPanel();
    this.loginPanel.getOptionsButton().addActionListener(this);
    this.loginPanel.getLinkLabel().addMouseListener(this.adapter);
    this.setContentPane(this.panel);

    this.pack();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    LauncherConfig config = LauncherConfig.load();
    if (config.getClientToken() == null) {
      ConfigManager.initConfig();
    }

    VersionManager.getVersionsFile();

    LauncherManager.setLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LauncherFrame.frameInstance = new LauncherFrame();
        LauncherFrame.frameInstance.setVisible(true);
      }
    });
  }

  @Override
  public void mousePerformed(final MouseEvent me) {
    this.adapter = new MouseAdapter() {
      final Object source = me.getSource();

      @Override
      public void mouseClicked(MouseEvent me) {
        if (source == loginPanel.getLinkLabel()) {
          try {
            Desktop.getDesktop().browse(URI.create(loginPanel.getLinkUrls()));
          } catch (IOException ioe) {
            throw new RuntimeException("Failed to launch the default browser", ioe);
          }
        }
      }
    };
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == this.panel.getLoginPanel().getOptionsButton()) {
      OptionsDialog optionsDialog = new OptionsDialog(LauncherFrame.frameInstance);
      optionsDialog.setVisible(true);
    }
  }
}
