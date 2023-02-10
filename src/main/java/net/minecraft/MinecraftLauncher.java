package net.minecraft;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LanguageHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
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
import net.minecraft.update.EState;
import net.minecraft.update.MinecraftUpdateImpl;

public class MinecraftLauncher extends Applet implements AppletStub {

  private static final long serialVersionUID = 1L;
  public final Map<String, String> parameters;
  private final Image bgImage;
  private final String titleString;
  private final String failedString;
  private MinecraftUpdateImpl update;
  private Applet applet;
  private boolean updateStarted;
  private boolean active;

  {
    this.titleString = LanguageHelper.getString("ml.string.title.text");
    this.failedString = LanguageHelper.getString("ml.string.failed.text");
  }

  public MinecraftLauncher() {
    this.parameters = new HashMap<>();
    this.bgImage = FileHelper.readImageFile(MinecraftLauncher.class, "icon/dirt.png");
  }

  public static boolean isMinecraftCached() {
    return MinecraftUpdateImpl.packageCached();
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
    int rectWidthPercentage = (this.update.getTotalPercentage() * rectWidth) / 100;
    int rectHeight = 5;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(rectX, rectY, rectWidth + 1, rectHeight);

    g2d.setColor(Color.GREEN.darker().darker());
    g2d.fillRect(rectX, rectY, rectWidthPercentage, rectHeight - 1);

    g2d.setColor(Color.GREEN.darker());
    g2d.fillRect(rectX, rectY + 1, rectWidthPercentage - 2, rectHeight - 4);
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

  @Override
  public URL getDocumentBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException murle) {
      LoggerHelper.logError(murle.getMessage(), murle, true);
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

      String title = this.titleString;
      if (this.update.isFatalErrorOccurred()) {
        title = this.failedString;
      }
      this.drawTitleString(title, panelWidth, panelHeight, g2d);

      String state = EState.getState().getMessage();
      if (this.update.isFatalErrorOccurred()) {
        state = this.update.getFatalErrorMessage();
      }
      this.drawStateString(state, panelWidth, panelHeight, g2d);

      String subtaskMessage = this.update.getSubtaskMessage();
      this.drawSubtaskMessageString(subtaskMessage, panelWidth, panelHeight, g2d);

      if (!this.update.isFatalErrorOccurred()) {
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

    String selectedVersion = LauncherConfig.instance.getSelectedVersion();
    try {
      String proxyPort = OptionsHelper.getPortsFromIds(selectedVersion);
      if (proxyPort != null) {
        System.setProperty("http.proxyHost", "betacraft.uk");
        System.setProperty("http.proxyPort", proxyPort);
      }
      System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    } catch (IOException ioe) {
      System.setProperty("http.proxyPort", "80");
    }

    this.update = new MinecraftUpdateImpl();
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
    if (this.updateStarted) {
      return;
    }

    Thread updateThread = new Thread(new Runnable() {
      @Override
      public void run() {
        MinecraftLauncher.this.update.run();
        if (!MinecraftLauncher.this.update.isFatalErrorOccurred()) {
          Applet instance = MinecraftLauncher.this.update.createAppletInstance();
          MinecraftLauncher.this.replace(instance);
        }
      }
    }, "UpdateDaemon");
    updateThread.setDaemon(true);
    updateThread.start();
    updateThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (MinecraftLauncher.this.applet == null) {
          MinecraftLauncher.this.repaint();
        }

        try {
          Thread.sleep(10L);
        } catch (InterruptedException ie) {
          LoggerHelper.logError("Failed to sleep current thread", ie, true);
        }
      }
    }, "PaintDaemon");
    updateThread.setDaemon(true);
    updateThread.start();

    this.updateStarted = true;
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
}
