package net.minecraft;

import ee.twentyten.config.Config;
import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.FilesManager;
import ee.twentyten.util.OptionsManager;
import ee.twentyten.util.ThreadManager;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JApplet;

public class GameLauncher extends JApplet implements AppletStub {

  private static final long serialVersionUID = 1L;
  public final Map<String, String> parameters;
  private final Image bgImage;
  private GameUpdater updater;
  private Applet applet;
  private boolean updating;
  private boolean active;

  public GameLauncher() {
    this.parameters = new HashMap<>();
    this.bgImage = FilesManager.readImageFile(GameLauncher.class, "icon/dirt.png");
  }

  public static boolean isGameCached() {
    return GameUpdater.packageCached();
  }

  private void drawTitleString(String s, int pWidth, int pHeight, Graphics2D g2d) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(s);
    int stringHeight = fm.getHeight();
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) - (stringHeight << 1);

    g2d.drawString(s, stringX, stringY);
  }

  private void drawStateString(String s, int pWidth, int pHeight, Graphics2D g2d) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(s);
    int stringHeight = fm.getHeight();
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) + (stringHeight);

    g2d.drawString(s, stringX, stringY);
  }

  private void drawSubtaskMessageString(String s, int pWidth, int pHeight, Graphics2D g2d) {
    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int stringWidth = fm.stringWidth(s);
    int stringHeight = fm.getHeight();
    int stringX = (pWidth >> 1 >> 1) - (stringWidth >> 1);
    int stringY = (pHeight >> 1 >> 1) + ((stringHeight) << 1);

    g2d.drawString(s, stringX, stringY);
  }

  private void drawPercentageRect(int pWidth, int pHeight, Graphics2D g2d) {
    int rectX = 64;
    int rectY = (pHeight >> 1) - rectX;
    int rectWidth = (pWidth >> 1) - 128;
    int rectWidthPercentage = (this.updater.getTotalPercentage() * rectWidth) / 100;
    int rectHeight = 5;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(rectX, rectY, rectWidth + 1, rectHeight);

    g2d.setColor(Color.GREEN.darker().darker());
    g2d.fillRect(rectX, rectY, rectWidthPercentage, rectHeight - 1);

    g2d.setColor(Color.GREEN.darker());
    g2d.fillRect(rectX, rectY + 1, rectWidthPercentage - 2, rectHeight - 4);
  }

  @Override
  public URL getDocumentBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException mue) {
      DebugLoggingManager.logError(this.getClass(), "Failed to create URL object", mue);
    }
    return null;
  }

  @Override
  public String getParameter(String name) {
    return this.parameters.containsKey(name) ? this.parameters.get(name) : null;
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public void update(Graphics g) {
    this.paint(g);
  }

  @Override
  public void paint(Graphics g) {
    int panelWidth = this.getWidth();
    int panelHeight = this.getHeight();
    int imageWidth = 32;
    int imageHeight = 32;
    int gridWidth = ((panelWidth + imageWidth) - 1) >> 5;
    int gridHeight = ((panelHeight + imageHeight) - 1) >> 5;

    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    VolatileImage compatibleVolatileImage = gc.createCompatibleVolatileImage(panelWidth >> 1,
        panelHeight >> 1, Transparency.TRANSLUCENT);

    Graphics2D g2d = compatibleVolatileImage.createGraphics();
    try {
      for (int gridIndex = 0; gridIndex < (gridWidth * gridHeight); gridIndex++) {
        int gridX = imageWidth * (gridIndex % gridWidth);
        int gridY = imageHeight * (gridIndex / gridWidth);
        g2d.drawImage(this.bgImage, gridX, gridY, imageWidth, imageHeight, this);
      }

      String title = "Updating Minecraft";
      if (this.updater.isFatalErrorOccurred()) {
        title = "Failed to launch";
      }
      this.drawTitleString(title, panelWidth, panelHeight, g2d);

      String state = this.updater.getStateDescription();
      if (this.updater.isFatalErrorOccurred()) {
        state = this.updater.getErrorMessage();
      }
      this.drawStateString(state, panelWidth, panelHeight, g2d);

      String subtaskMessage = this.updater.getSubtaskMessage();
      this.drawSubtaskMessageString(subtaskMessage, panelWidth, panelHeight, g2d);

      if (!this.updater.isFatalErrorOccurred()) {
        this.drawPercentageRect(panelWidth, panelHeight, g2d);
      }
    } finally {
      g2d.dispose();
    }

    g.drawImage(compatibleVolatileImage, 0, 0, panelWidth, panelHeight, 0, 0, panelWidth >> 1,
        panelHeight >> 1, this);
  }

  public void init(String username, String sessionId, String hasPaid) {
    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);
    this.parameters.put("haspaid", hasPaid);

    String formattedParameters = String.format("%s:%s:%s", username, sessionId, hasPaid);
    DebugLoggingManager.logInfo(this.getClass(), formattedParameters);

    String selectedVersion = Config.instance.getSelectedVersion();
    try {
      String proxyPort = OptionsManager.getPortsFromIds(selectedVersion);
      if (proxyPort != null) {
        System.setProperty("http.proxyHost", "betacraft.uk");
        System.setProperty("http.proxyPort", proxyPort);
      }
      System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    } catch (IOException ioe) {
      DebugLoggingManager.logError(this.getClass(), "Failed to get proxy port", ioe);
    }

    String httpProxyHost = System.getProperty("http.proxyHost");
    String httpProxyPort = System.getProperty("http.proxyPort");
    String legacyMergeSort = System.getProperty("java.util.Arrays.useLegacyMergeSort");
    String formattedSystemProperties = String.format("%s:%s:%s", httpProxyHost, httpProxyPort,
        legacyMergeSort);
    DebugLoggingManager.logInfo(this.getClass(), formattedSystemProperties);

    this.updater = new GameUpdater();
  }

  @Override
  public void init() {
    if (this.applet != null) {
      this.applet.init();
      return;
    }
    this.init(this.getParameter("username"), this.getParameter("sessionid"),
        this.getParameter("haspaid"));
  }

  @Override
  public void start() {
    if (this.applet != null) {
      this.applet.start();
      return;
    }
    if (this.updating) {
      return;
    }

    Thread updateThread = ThreadManager.createDaemonThread("UpdateDaemon", new Runnable() {
      @Override
      public void run() {
        GameLauncher.this.updater.run();
        if (!GameLauncher.this.updater.isFatalErrorOccurred()) {
          Applet instance = GameLauncher.this.updater.createAppletInstance();
          GameLauncher.this.replace(instance);
        }
      }
    });
    updateThread.start();
    updateThread = ThreadManager.createDaemonThread("PaintDaemon", new Runnable() {
      @Override
      public void run() {
        while (GameLauncher.this.applet == null) {
          GameLauncher.this.repaint();
        }
        try {
          Thread.sleep(10L);
        } catch (InterruptedException ie) {
          DebugLoggingManager.logError(this.getClass(),
              "Failed to temporarily cease thread execution", ie);
        }
      }
    });
    updateThread.start();

    this.updating = true;
  }

  @Override
  public void stop() {
    if (this.applet != null) {
      this.active = false;
      this.applet.stop();
    }
  }

  @Override
  public void destroy() {
    if (this.applet != null) {
      this.applet.destroy();
    }
  }

  @Override
  public void appletResize(int width, int height) {
    if (this.applet != null) {
      this.applet.resize(width, height);
      return;
    }
    this.setSize(width, height);
  }

  private void replace(Applet applet) {
    this.applet = applet;

    applet.setStub(this);
    applet.setSize(this.getWidth(), this.getHeight());
    this.setLayout(new BorderLayout());
    this.add(applet, BorderLayout.CENTER);

    applet.init();
    this.active = true;
    applet.start();

    this.revalidate();
  }
}
