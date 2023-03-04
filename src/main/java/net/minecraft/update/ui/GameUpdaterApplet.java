package net.minecraft.update.ui;

import ee.twentyten.log.ELevel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.VersionUtils;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.VolatileImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JApplet;
import javax.swing.Timer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.update.GameUpdaterImpl;

public class GameUpdaterApplet extends JApplet implements AppletStub {

  @Getter
  @Setter
  private static GameUpdaterApplet instance;
  public Map<String, String> parameters;
  public Applet minecraftApplet;
  private GameUpdaterImpl updater;
  private boolean appletActive;
  private boolean isUpdateAvailable;

  {
    this.parameters = new HashMap<>();
    this.minecraftApplet = null;
    this.appletActive = false;
    this.isUpdateAvailable = false;
  }

  public GameUpdaterApplet() {
    GameUpdaterApplet.setInstance(this);
  }

  public void init(String username, String sessionId) {
    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);

    File versionsFile = new File(VersionUtils.versionsDirectory, "versions.json");
    String selectedVersion = ConfigUtils.getInstance().getSelectedVersion();
    if (versionsFile.exists() && selectedVersion != null) {
      int proxyPort = VersionUtils.getProxyPort(selectedVersion);
      System.setProperty("http.proxyHost", "betacraft.uk");
      System.setProperty("http.proxyPort", String.valueOf(proxyPort));
      System.setProperty("java.util.Arrays.useLegacyMergeSort", String.valueOf(true));
    }
    this.updater = new GameUpdaterImpl();
  }

  private void replace(Applet applet) {
    this.minecraftApplet = applet;
    applet.setStub(this);
    applet.setSize(this.getWidth(), this.getHeight());

    LoggerUtils.logPrintln();

    this.add(this.minecraftApplet);
    applet.init();
    this.appletActive = true;
    applet.start();

    this.revalidate();
    this.repaint();
  }

  private void drawTitleString(Graphics2D g2d, String title, int width, int height) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(title, titleX, titleY);
  }

  private void drawStateMessageString(Graphics2D g2d, String title, int width, int height) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight);
    g2d.drawString(title, titleX, titleY);
  }

  private void drawTaskMessageString(Graphics2D g2d, String title, int width, int height) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(title);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight << 1);
    g2d.drawString(title, titleX, titleY);
  }

  private void drawPercentageRect(Graphics2D g2d, int width, int height) {
    int rectX = 64;
    int rectY = (height >> 1) - rectX;
    int rectWidth = (width >> 1) - 128;
    int rectHeight = 5;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(rectX, rectY, rectWidth + 1, rectHeight);

    g2d.setColor(Color.GREEN.darker().darker());
    g2d.fillRect(rectX, rectY, (this.updater.getPercentage() * (rectWidth)) / 100,
        rectHeight - 1);

    g2d.setColor(Color.GREEN.darker());
    g2d.fillRect(rectX, rectY + 1, ((this.updater.getPercentage() * (rectWidth)) / 100) - 2,
        rectHeight - 4);
  }

  @Override
  public boolean isActive() {
    return this.appletActive;
  }

  @Override
  public URL getDocumentBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create document base URL", murle, ELevel.ERROR);
    }
    return null;
  }

  @Override
  public URL getCodeBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create code base URL", murle, ELevel.ERROR);
    }
    return null;
  }

  @Override
  public String getParameter(String name) {
    return this.parameters.containsKey(name) ? this.parameters.get(name) : null;
  }

  /*
  @Override
  public AppletContext getAppletContext() {
    return null;
  }
  */

  @Override
  public void init() {
    if (this.minecraftApplet != null) {
      this.minecraftApplet.init();
      return;
    }
    this.init(this.getParameter("username"), this.getParameter("sessionid"));
  }

  @Override
  public void start() {
    if (this.minecraftApplet != null) {
      this.minecraftApplet.start();
      return;
    }
    if (this.isUpdateAvailable) {
      return;
    }

    Thread updaterThread = new Thread(new Runnable() {
      @Override
      public void run() {
        GameUpdaterApplet.this.updater.run();
        if (!GameUpdaterApplet.this.updater.isFatalErrorOccurred()) {
          Applet minecraftApplet = GameUpdaterApplet.this.updater.loadMinecraftApplet();
          GameUpdaterApplet.this.replace(minecraftApplet);
        }
      }
    });
    updaterThread.setName(MessageFormat.format("updater-{0}", updaterThread.getId()));
    updaterThread.setDaemon(true);
    updaterThread.start();
    this.isUpdateAvailable = true;

    final Timer repaintTimer = new Timer(10, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (GameUpdaterApplet.this.minecraftApplet == null) {
          GameUpdaterApplet.this.update(GameUpdaterApplet.this.getGraphics());
        } else {
          ((Timer) source).stop();
        }
      }
    });
    repaintTimer.start();
  }

  @Override
  public void stop() {
    if (this.minecraftApplet != null) {
      this.minecraftApplet.stop();
      this.appletActive = false;
    }
  }

  @Override
  public void destroy() {
    if (this.minecraftApplet != null) {
      this.minecraftApplet.destroy();
    }
  }

  @Override
  public void update(Graphics g) {
    this.paint(g);
  }

  @Override
  public void paint(Graphics g) {
    if (this.minecraftApplet != null) {
      return;
    }

    Image bgImage = FileUtils.readImageResource("icon/dirt.png", LauncherPanel.class);
    if (bgImage == null) {
      bgImage = this.createImage(1, 1);
    }

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    int appletWidth = this.getWidth();
    int appletHeight = this.getHeight();
    VolatileImage vImg = gc.createCompatibleVolatileImage(appletWidth >> 1, appletHeight >> 1,
        Transparency.OPAQUE);

    Graphics2D g2d = vImg.createGraphics();
    try {
      int bgWidth = bgImage.getWidth(this) << 1;
      int bgHeight = bgImage.getHeight(this) << 1;
      int gridWidth = (appletWidth + bgWidth) >> 5;
      int gridHeight = (appletHeight + bgHeight) >> 5;
      for (int i = 0; i < (gridWidth * gridHeight); i++) {
        int gridX = (i % gridWidth) << 5;
        int gridY = (i / gridWidth) << 5;
        g2d.drawImage(bgImage, gridX, gridY, bgWidth, bgHeight, this);
      }

      String title = LanguageUtils.getString(LanguageUtils.getBundle(), "gua.string.title");
      if (this.updater.isFatalErrorOccurred()) {
        title = LanguageUtils.getString(LanguageUtils.getBundle(), "gua.string.title.fatalError");
      }
      this.drawTitleString(g2d, title, appletWidth, appletHeight);

      String stateMessage = this.updater.getStateMessage();
      this.drawStateMessageString(g2d, stateMessage, appletWidth, appletHeight);

      String taskMessage = this.updater.getTaskMessage();
      this.drawTaskMessageString(g2d, taskMessage, appletWidth, appletHeight);

      if (!this.updater.isFatalErrorOccurred()) {
        this.drawPercentageRect(g2d, appletWidth, appletHeight);
      }
    } finally {
      g2d.dispose();
    }
    g.drawImage(vImg, 0, 0, appletWidth, appletHeight, this);
  }

  @Override
  public void appletResize(int width, int height) {
    if (this.minecraftApplet != null) {
      this.minecraftApplet.resize(width, height);
      return;
    }
    super.setSize(width, height);
  }
}