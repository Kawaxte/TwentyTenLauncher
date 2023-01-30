package net.minecraft.application;

import ee.twentyten.util.FilesManager;
import ee.twentyten.util.ThreadManager;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JApplet;

public class MinecraftLauncher extends JApplet implements AppletStub {

  public final Map<String, String> parameters = new HashMap<>();
  private MinecraftUpdaterImpl updater;
  private JApplet applet;
  private Image bgImage;
  private boolean active;
  private boolean updating;

  public boolean isActive() {
    return this.active;
  }

  public URL getDocumentBase() {
    try {
      return new URL("http://www.minecraft.net/game/");
    } catch (MalformedURLException mue) {
      return null;
    }
  }

  public String getParameter(String value) {
    return this.parameters.containsKey(value) ? this.parameters.get(value) : null;
  }

  @Override
  public void appletResize(int width, int height) {
    if (this.applet != null) {
      this.applet.resize(width, height);
      return;
    }
    this.setSize(width, height);
  }

  public void init(String username, String sessionId) {
    this.bgImage = FilesManager.readImageFile(MinecraftLauncher.class, "icon/dirt.png");

    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);
    this.parameters.put("haspaid", sessionId != null ? "true" : "false");

    this.updater = new MinecraftUpdaterImpl();
  }

  public void init() {
    if (this.applet != null) {
      this.applet.init();
      return;
    }
    this.init(this.getParameter("username"), this.getParameter("sessionid"));
  }

  public void start() {
    if (this.applet != null) {
      this.applet.start();
      return;
    }
    if (this.updating) {
      return;
    }

    ThreadManager.createDaemonThread(new Runnable() {
      @Override
      public void run() {
        // TODO: MINECRAFT APPLET INSTANCE
      }
    }).start();
  }

  public void stop() {
    if (this.applet != null) {
      this.applet.stop();
    }
    this.active = false;
  }

  public void destroy() {
    if (this.applet != null) {
      this.applet.destroy();
    }
    this.active = false;
  }

  private void replace(JApplet applet) {
    this.applet = applet;
    this.setLayout(new BorderLayout());
    this.add(applet, BorderLayout.CENTER);
    this.applet.init();
    this.applet.start();
    this.repaint();
  }
}
