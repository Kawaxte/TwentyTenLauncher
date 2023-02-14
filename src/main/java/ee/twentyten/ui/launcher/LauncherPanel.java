package ee.twentyten.ui.launcher;

import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.VolatileImage;
import java.io.File;
import javax.swing.JPanel;

public class LauncherPanel extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final String errorLabelFailedText;
  private final String errorLabelOutdatedText;
  private final String errorLabelConnectionText;
  private final Image bgImage;
  private final LauncherLoginPanel launcherLoginPanel;

  {
    this.bgImage = FileHelper.readImageFile(
        LauncherPanel.class, "icon/dirt.png").get();

    this.errorLabelFailedText = LauncherLanguage
        .getString("lp.label.errorLabel.failed");
    this.errorLabelOutdatedText = LauncherLanguage
        .getString("lp.label.errorLabel.outdated");
    this.errorLabelConnectionText = LauncherLanguage
        .getString("lp.label.errorLabel.connection");
  }

  public LauncherPanel(
      LayoutManager layout,
      boolean isDoubleBuffered
  ) {
    super(layout, isDoubleBuffered);

    Dimension panelSize = new Dimension(854, 480);
    this.setPreferredSize(panelSize);

    BorderLayout borderLayout = new BorderLayout(0, 8);
    this.launcherLoginPanel = new LauncherLoginPanel(
        borderLayout, true
    );
    this.launcherLoginPanel
        .getOptionsButton()
        .addActionListener(this);
    this.launcherLoginPanel
        .getLoginButton()
        .addActionListener(this);
    this.add(this.launcherLoginPanel);
  }

  private void openOptionsDialog() {
    String optionsTitleText = LauncherLanguage
        .getString("od.string.title");

    OptionsDialog optionsDialog = new OptionsDialog(
        optionsTitleText, LauncherFrame.instance
    );
    optionsDialog.setVisible(true);
  }

  public void showNoNetworkMessage(
      String message
  ) {
    this.removeAll();

    /*
    this.add(this.offlinePanel);
    this.offlinePanel.getErrorLabel().setText(message);

    if (!MinecraftLauncher.isMinecraftCached()) {
      this.offlinePanel.add(this.offlinePanel.getPlayOnlineLabel(),
          BorderLayout.CENTER);
    } else {
      this.offlinePanel.remove(this.offlinePanel.getPlayOnlineLabel());
    }
    this.offlinePanel.getPlayOfflineButton()
        .setEnabled(MinecraftLauncher.isMinecraftCached());
    */

    this.revalidate();
    this.repaint();
  }

  private void drawTitleString(
      String title,
      int pWidth,
      int pHeight,
      Graphics2D g2d
  ) {

    /* Set the font and color */
    Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    g2d.setFont(titleFont);
    g2d.setColor(Color.LIGHT_GRAY);

    /* Get the font metrics */
    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(title);
    int stringHeight = fm.getHeight();

    /* Bit shifting is used to divide and multiply by 2 because it is faster
     * than using the division and multiplication operators */
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) - (stringHeight << 1);

    /* Draw the string */
    g2d.drawString(title, stringX, stringY);
  }

  @Override
  protected void paintComponent(
      Graphics g
  ) {
    super.paintComponent(g);

    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    int imageWidth = 32;
    int imageHeight = 32;

    /* Bit shifting is used to divide by 32 because it is faster than using
     * the division operator to divide by 32 */
    int gridWidth = ((panelWidth + imageWidth) - 1) >> 5;
    int gridHeight = ((panelHeight + imageHeight) - 1) >> 5;

    /* Create a compatible volatile image */
    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    VolatileImage compatVolatileImage = gc.createCompatibleVolatileImage(
        panelWidth >> 1, panelHeight >> 1,
        Transparency.TRANSLUCENT);

    /* Create a graphics object from the volatile image */
    Graphics2D g2d = compatVolatileImage.createGraphics();
    try {

      /* Loop through the grid where each grid cell is 32x32 pixels */
      for (int gridIndex = 0; gridIndex < (gridWidth * gridHeight);
          gridIndex++) {

        /* Modulus and division are used to get the x and y coordinates of
         * the grid */
        int gridX = imageWidth * (gridIndex % gridWidth);
        int gridY = imageHeight * (gridIndex / gridWidth);

        /* Draw the background image */
        g2d.drawImage(
            this.bgImage,
            gridX, gridY,
            imageWidth, imageHeight,
            this
        );
      }

      String title = "TwentyTen Launcher";
      this.drawTitleString(title, panelWidth, panelHeight, g2d);
    } finally {

      /* Dispose of the graphics object to free up resources */
      g2d.dispose();
    }

    /* Draw the volatile image to the screen */
    g.drawImage(
        compatVolatileImage,
        0, 0,
        panelWidth, panelHeight,
        0, 0,
        panelWidth >> 1, panelHeight >> 1,
        this
    );
  }

  @Override
  public void actionPerformed(
      ActionEvent event
  ) {

    /* Get the source of the event */
    Object source = event.getSource();

    /* Check if the source is the login button */
    if (source == this.launcherLoginPanel.getLoginButton()) {
      //TODO: SIGNING IN WITH YGGDRASIL
    }

    /* Check if the source is the options button */
    if (source == this.launcherLoginPanel.getOptionsButton()) {

      /* Check if the versions directory exists */
      File versionsDirectory = new File(
          FileHelper.workingDirectory, "versions");
      if (!versionsDirectory.exists()) {
        boolean isVersionsDirectoryCreated = versionsDirectory.mkdirs();
        if (!isVersionsDirectoryCreated) {
          LoggerHelper.logError(
              "Failed to create versions directory",
              true
          );
        }
      }

      /* Check if the versions file exists */
      File versionsFile = new File(versionsDirectory, "versions.json");
      if (!versionsFile.exists()) {

        /* Calculate the time since the file was last modified */
        long lastModified =
            System.currentTimeMillis() - versionsFile.lastModified();

        /* Check if the file is older than the cache expiration time */
        if (lastModified > FileHelper.cacheExpirationTime) {

          /* Download the versions file */
          FileHelper.downloadFile(
              OptionsHelper.VERSIONS_JSON_URL, versionsFile
          );
        }
        this.openOptionsDialog();
        return;
      }
      this.openOptionsDialog();
    }
  }
}