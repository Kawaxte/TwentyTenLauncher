/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.kawaxte.launcher.ui;

import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.minecraft.EState;
import ch.kawaxte.launcher.minecraft.MinecraftUpdate;
import ch.kawaxte.launcher.minecraft.MinecraftUpdateWorker;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import ch.kawaxte.launcher.util.MinecraftUtils;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftAppletWrapper extends JApplet implements AppletStub {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER;
  private static MinecraftAppletWrapper instance;

  static {
    LOGGER = LoggerFactory.getLogger(MinecraftAppletWrapper.class);
  }

  private final transient Image lightDirtBgImg;
  private final Map<String, String> parameters;
  private transient BufferedImage bImg;
  @Getter @Setter private transient ClassLoader mcAppletClassLoader;
  @Getter @Setter private String taskStateMessage;
  @Getter private String taskProgressMessage;
  @Getter @Setter private int taskState;
  @Getter @Setter private int taskProgress;
  @Getter @Setter private boolean updaterTaskStarted;
  @Getter @Setter private boolean updaterTaskErrored;
  private Applet minecraftApplet;
  private boolean active;

  public MinecraftAppletWrapper(String username, String sessionId, boolean demo) {
    setInstance(this);

    URL lightDirtBgImgUrl =
        Optional.ofNullable(
                this.getClass()
                    .getClassLoader()
                    .getResource("assets/gui/light_dirt_background.png"))
            .orElseThrow(() -> new NullPointerException("lightDirtBgImgUrl cannot be null"));

    this.parameters = new HashMap<>();
    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);

    String selectedVersion = (String) LauncherConfig.get(4);
    if (IntStream.rangeClosed(3, 5)
        .anyMatch(i -> selectedVersion.contains(String.format("1.%d.", i)))) {
      this.parameters.put("demo", String.valueOf(demo));
    }

    this.lightDirtBgImg = this.getToolkit().getImage(lightDirtBgImgUrl);
    this.taskState = EState.INITIALISE.ordinal();
    this.taskStateMessage = EState.INITIALISE.getMessage();
    this.taskProgressMessage = "";
    this.taskProgress = 0;

    String[] proxies = LauncherUtils.getProxyHostAndPort();
    System.setProperty("http.proxyHost", proxies[0]);
    System.setProperty("http.proxyPort", proxies[1]);
    System.setProperty("java.util.Arrays.useLegacyMergeSort", String.valueOf(true));

    MinecraftUtils.reassignOutputStream(username);
  }

  public static MinecraftAppletWrapper getInstance() {
    return instance;
  }

  private static void setInstance(MinecraftAppletWrapper maw) {
    instance = maw;
  }

  public void setTaskProgressMessage(String message, Object... args) {
    if (Objects.isNull(message)) {
      this.taskProgressMessage = "";
      return;
    }

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
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

    new MinecraftUpdateWorker(MinecraftUpdate.getGenericUrls()).execute();
    new Timer(
            10,
            event -> {
              Timer source = (Timer) event.getSource();
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

    int appletWidth = this.getWidth();
    int appletHeight = this.getHeight();
    int lightDirtBgImgWidth = this.lightDirtBgImg.getWidth(this) << 1;
    int lightDirtBgImgHeight = this.lightDirtBgImg.getHeight(this) << 1;

    Graphics2D g2d = (Graphics2D) g;
    GraphicsConfiguration configuration = g2d.getDeviceConfiguration();

    if (Objects.isNull(this.bImg)
        || (!Objects.equals(this.bImg.getWidth(this), appletWidth)
            || !Objects.equals(this.bImg.getHeight(this), appletHeight))) {
      this.bImg =
          configuration.createCompatibleImage(
              appletWidth >> 1, appletHeight >> 1, Transparency.OPAQUE);
    }

    Graphics2D g2dBuffered = this.bImg.createGraphics();
    g2dBuffered.setComposite(AlphaComposite.Clear);
    g2dBuffered.fillRect(0, 0, appletWidth, appletHeight);
    g2dBuffered.setComposite(AlphaComposite.SrcOver);

    try {
      int gridWidth = (appletWidth + lightDirtBgImgWidth) >> 5;
      int gridHeight = (appletHeight + lightDirtBgImgHeight) >> 5;
      IntStream.range(0, (gridWidth * gridHeight))
          .forEach(
              i -> {
                int gridX = (i % gridWidth) << 5;
                int gridY = (i / gridWidth) << 5;
                g2dBuffered.drawImage(
                    this.lightDirtBgImg,
                    gridX,
                    gridY,
                    lightDirtBgImgWidth,
                    lightDirtBgImgHeight,
                    this);
              });

      String selectedLanguage = (String) LauncherConfig.get(0);
      UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);

      String title =
          updaterTaskErrored
              ? bundle.getString(LauncherLanguageUtils.getGAWKeys()[1])
              : bundle.getString(LauncherLanguageUtils.getGAWKeys()[0]);
      this.drawTitleString(title, appletWidth, appletHeight, g2dBuffered);
      this.drawStateString(taskStateMessage, appletWidth, appletHeight, g2dBuffered);
      this.drawProgressString(taskProgressMessage, appletWidth, appletHeight, g2dBuffered);
      this.drawTaskProgressRect(appletWidth, appletHeight, g2dBuffered);
    } finally {
      g2dBuffered.dispose();
    }

    g2d.drawImage(bImg, 0, 0, appletWidth, appletHeight, this);
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

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawStateString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.PLAIN, 12f));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) + (titleHeight);
    g2d.drawString(s, titleX, titleY);
  }

  private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
    g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
    g2d.setColor(Color.LIGHT_GRAY);

    FontMetrics fm = g2d.getFontMetrics();
    int titleWidth = fm.stringWidth(s);
    int titleHeight = fm.getHeight();
    int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
    int titleY = (height >> 1 >> 1) - (titleHeight << 1);
    g2d.drawString(s, titleX, titleY);
  }

  @Override
  public void appletResize(int width, int height) {
    super.resize(width, height);
    if (Objects.nonNull(this.minecraftApplet)) {
      this.minecraftApplet.setSize(width, height);
    }
  }

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
