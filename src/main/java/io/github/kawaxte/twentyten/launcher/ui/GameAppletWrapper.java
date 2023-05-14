package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.game.EState;
import io.github.kawaxte.twentyten.launcher.game.GameUpdater;
import io.github.kawaxte.twentyten.launcher.game.GameUpdaterWorker;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.JApplet;
import javax.swing.Timer;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameAppletWrapper extends JApplet implements AppletStub {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER;
  public static GameAppletWrapper instance;

  static {
    LOGGER = LogManager.getLogger(GameAppletWrapper.class);
  }

  private final Map<String, String> parameters;
  @Getter @Setter private ClassLoader mcAppletClassLoader;
  @Getter @Setter private String taskStateMessage;
  @Getter private String taskProgressMessage;
  @Getter @Setter private int taskState;
  @Getter @Setter private int taskProgress;
  @Getter @Setter private boolean updaterTaskStarted;
  @Getter @Setter private boolean updaterTaskErrored;
  private Applet minecraftApplet;
  private boolean active;

  {
    this.parameters = new HashMap<>();
    this.taskState = EState.INITIALISE.ordinal();
    this.taskStateMessage = EState.INITIALISE.getMessage();
    this.taskProgressMessage = "";
    this.taskProgress = 0;
  }

  public GameAppletWrapper(String username, String sessionId) {
    GameAppletWrapper.instance = this;

    if (Objects.isNull(username)) {
      username =
          new StringBuilder()
              .append("Player")
              .append(System.currentTimeMillis() % 1000L)
              .toString();
    }
    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);

    val hostAndPort = LauncherUtils.getProxyHostAndPort();
    if (Objects.nonNull(hostAndPort)) {
      System.setProperty("http.proxyHost", hostAndPort[0]);
      System.setProperty("http.proxyPort", hostAndPort[1]);
    }
    System.setProperty("java.util.Arrays.useLegacyMergeSort", String.valueOf(true));
  }

  public void setTaskProgressMessage(String message, Object... args) {
    if (Objects.isNull(message)) {
      this.taskProgressMessage = "";
      return;
    }

    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.taskProgressMessage = MessageFormat.format(bundle.getString(message), args);
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public URL getDocumentBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL for Minecraft", murle);
    }
    return null;
  }

  @Override
  public String getParameter(String name) {
    return this.parameters.getOrDefault(name, null);
  }

  @Override
  public void start() {
    if (Objects.nonNull(this.minecraftApplet)) {
      return;
    }

    new GameUpdaterWorker(GameUpdater.getUrls()).execute();
    new Timer(
            10,
            event -> {
              val source = (Timer) event.getSource();
              if (Objects.isNull(minecraftApplet)) {
                repaint();
              } else {
                source.stop();
              }
            })
        .start();
  }

  @Override
  public void stop() {
    if (Objects.nonNull(this.minecraftApplet)) {
      this.minecraftApplet.stop();
      this.active = false;
    }
  }

  @Override
  public void destroy() {
    if (Objects.nonNull(this.minecraftApplet)) {
      this.minecraftApplet.destroy();
    }
  }

  @Override
  public void paint(Graphics g) {
    if (Objects.nonNull(this.minecraftApplet)) {
      return;
    }

    val bgImageUrl =
        Optional.ofNullable(this.getClass().getClassLoader().getResource("dirt.png"))
            .orElseThrow(() -> new NullPointerException("bgImageUrl cannot be null"));
    val bgImage = this.getToolkit().getImage(bgImageUrl);
    int bgImageWidth = bgImage.getWidth(this) << 1;
    int bgImageheight = bgImage.getHeight(this) << 1;
    int appletWidth = this.getWidth();
    int appletHeight = this.getHeight();

    val g2d = (Graphics2D) g;
    val deviceConfiguration = g2d.getDeviceConfiguration();

    val bufferedImage =
        deviceConfiguration.createCompatibleImage(
            appletWidth >> 1, appletHeight >> 1, Transparency.OPAQUE);
    val g2dBuffered = bufferedImage.createGraphics();
    g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
    try {
      int gridWidth = (appletWidth + bgImageWidth) >> 5;
      int gridHeight = (appletHeight + bgImageheight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .parallel()
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                g2dBuffered.drawImage(bgImage, gridX, gridY, bgImageWidth, bgImageheight, this);
              });
      g2dBuffered.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

      val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
      val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
      val title =
          updaterTaskErrored
              ? bundle.getString("gaw.updaterErrored")
              : bundle.getString("gaw.updaterStarted");
      this.drawTitleString(title, appletWidth, appletHeight, g2dBuffered);
      this.drawStateString(taskStateMessage, appletWidth, appletHeight, g2dBuffered);
      this.drawProgressString(taskProgressMessage, appletWidth, appletHeight, g2dBuffered);
      this.drawTaskProgressRect(appletWidth, appletHeight, g2dBuffered);
    } finally {
      g2dBuffered.dispose();
    }

    g2d.drawImage(bufferedImage, 0, 0, appletWidth, appletHeight, this);
  }

  private void drawTaskProgressRect(int width, int height, Graphics2D g2d) {
    int rectX = 64;
    int rectY = (height >> 1) - rectX;
    int rectWidth = (width >> 1) - 128;
    int rectHeight = 5;

    g2d.setColor(Color.BLACK);
    g2d.fillRect(rectX, rectY, rectWidth + 1, rectHeight);

    g2d.setColor(!updaterTaskErrored ? Color.GREEN.darker().darker() : Color.RED.darker().darker());
    g2d.fillRect(rectX, rectY, (taskProgress * (rectWidth)) / 100, rectHeight - 1);

    g2d.setColor(!updaterTaskErrored ? Color.GREEN.darker() : Color.RED.darker());
    g2d.fillRect(rectX, rectY + 1, ((taskProgress * rectWidth) / 100) - 2, rectHeight - 4);
  }

  private void drawProgressString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.PLAIN, 12f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawStateString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.PLAIN, 12f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
    g2d.setColor(Color.LIGHT_GRAY);

    val fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }

  @Override
  public void appletResize(int width, int height) {}

  public void replace(Applet applet) {
    this.minecraftApplet = applet;
    applet.setStub(this);
    applet.setSize(this.getWidth(), this.getHeight());

    this.setLayout(new BorderLayout());
    this.setBackground(Color.BLACK);
    this.setContentPane(applet);

    applet.init();
    this.active = true;
    applet.start();

    this.repaint();
  }
}
