package net.minecraft;

import ee.twentyten.util.DebugLoggingManager;
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

  private static final long serialVersionUID = 1L;
  public final Map<String, String> parameters;
  private final Image bgImage;
  private MinecraftUpdater updater;
  private JApplet applet;
  private boolean updating;
  private int context;
  private boolean active;

  public MinecraftLauncher() {
    this.parameters = new HashMap<>();
    this.bgImage = FilesManager.readImageFile(MinecraftLauncher.class, "icon/dirt.png");
    this.updating = false;
    this.context = 0;
    this.active = false;
  }

  @Override
  public boolean isActive() {
    if (this.context == 0) {
      this.context = this.getAppletContext() != null ? 1 : -1;
    }
    return this.context == -1 ? this.active : super.isActive();
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
  public void appletResize(int width, int height) {
    if (this.applet != null) {
      this.applet.resize(width, height);
      return;
    }
    this.setSize(width, height);
  }

  public void init(String username, String sessionId) {
    String hasPaid = sessionId != null ? "true" : "false";
    this.parameters.put("username", username);
    this.parameters.put("sessionid", sessionId);
    this.parameters.put("haspaid", hasPaid);

    String formattedParameters = String.format("%s:%s:%s", username, sessionId, hasPaid);
    DebugLoggingManager.logInfo(this.getClass(), formattedParameters);

    this.updater = new MinecraftUpdater();
  }

  @Override
  public void init() {
    if (this.applet != null) {
      this.applet.init();
      return;
    }
    this.init(this.getParameter("username"), this.getParameter("sessionid"));
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

    ThreadManager.createDaemonThread(new Runnable() {
      @Override
      public void run() {
        MinecraftLauncher.this.updater.run();
        if (!MinecraftLauncher.this.updater.isErrorOccurred()) {
          replace(MinecraftLauncher.this.updater.createAppletInstance());
        }

        while (MinecraftLauncher.this.applet == null) {
          MinecraftLauncher.this.repaint();
          try {
            wait();
          } catch (InterruptedException ie) {
            DebugLoggingManager.logError(this.getClass(), "Failed to cause thread to wait", ie);
          }
        }
      }
    }).start();
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

  private void replace(JApplet applet) {
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
