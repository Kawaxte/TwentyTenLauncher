package net.minecraft;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MCLauncher extends Applet implements AppletStub {
    private final MCLauncherGraphics mcLauncherGraphics = new MCLauncherGraphics(this);
    public Map<String, String> customParameters = new HashMap<>();
    private Image image;
    private VolatileImage volatileImage;
    private boolean active = false;
    private boolean minecraftUpdaterStarted = false;
    private Applet applet;
    private MCUpdater minecraftUpdater;

    public MCLauncher() {
        System.setProperty("http.proxyHost", "betacraft.uk");
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    public void paint(Graphics g2) {
        mcLauncherGraphics.paint(g2);
    }

    public void update(Graphics g) {
        mcLauncherGraphics.update(g);
    }

    public boolean isActive() {
        return active;
    }

    public void init(String username, String sessionId) {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("dirt.png"))).getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.customParameters.put("username", username);
        this.customParameters.put("sessionid", sessionId);
        this.minecraftUpdater = new MCUpdater();
    }

    public void init() {
        if (this.applet != null) {
            this.applet.init();
            return;
        }
        this.init(this.getParameter("username"), this.getParameter("sessionid"));
    }

    public void start() {
        if (minecraftUpdaterStarted) {
            if (this.applet == null) {
                return;
            }
            this.applet.start();
        } else {
            Thread thread = new Thread(minecraftUpdater) {
                public void run() {
                    minecraftUpdater.run();
                    try {
                        if (!minecraftUpdater.fatalError) {
                            try {
                                replace(minecraftUpdater.createApplet());
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
            thread = new Thread(() ->
            {
                while (this.applet == null)
                {
                    this.repaint();
                }
            });
            thread.setDaemon(true);
            thread.start();
            this.minecraftUpdaterStarted = true;
        }
    }

    public void stop() {
        if (this.applet != null) {
            this.active = false;
            this.applet.stop();
        }
    }

    public void destroy() {
        if (this.applet != null) {
            this.applet.destroy();
        }
    }

    public void replace(Applet applet) {
        this.applet = applet;
        this.setLayout(new BorderLayout());
        this.add(applet, "Center");
        applet.setStub(this);
        applet.setSize(this.getWidth(), this.getHeight());
        applet.init();
        applet.start();
        this.active = true;
        this.validate();
    }

    public void appletResize(int width, int height) {
        if (this.applet != null) {
            this.applet.resize(width, height);
        }
    }

    public boolean canPlayOffline() {
        return this.minecraftUpdater.canPlayOffline();
    }

    public URL getCodeBase() {
        try {
            return new URL("https://www.minecraft.net/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public URL getDocumentBase() {
        try {
            return new URL("https://github.com/sojlabjoi");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getParameter(String name) {
        if (this.customParameters != null) {
            return this.customParameters.get(name);
        } else {
            try {
                return super.getParameter(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public MCUpdater getMinecraftUpdater() {
        return this.minecraftUpdater;
    }

    public Applet getApplet() {
        return this.applet;
    }

    public Image getImage() {
        return image;
    }

    public VolatileImage getVolatileImage() {
        return this.volatileImage;
    }

    public void setVolatileImage(VolatileImage volatileImage) {
        this.volatileImage = volatileImage;
    }
}