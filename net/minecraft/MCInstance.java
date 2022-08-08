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

public class MCInstance extends Applet implements AppletStub {
    private static final long serialVersionUID = 1L;
    private final MCInstanceGraphics minecraftInstanceGraphics = new MCInstanceGraphics(this);
    public final Map<String, String> parameters = new HashMap<>();
    private boolean active = false;
    private boolean minecraftUpdateStarted = false;
    private Image image;
    private VolatileImage volatileImage;
    private Applet applet;
    private MCUpdate minecraftUpdate;

    public MCInstance() {
        System.setProperty("http.proxyHost", "betacraft.uk");
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    public void paint(Graphics g2) {
        minecraftInstanceGraphics.paint(g2);
    }

    public void update(Graphics g) {
        minecraftInstanceGraphics.update(g);
    }

    public void init(String username, String sessionId) {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("dirt.png"))).getScaledInstance(32, 32, Image.SCALE_FAST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.parameters.put("username", username);
        this.parameters.put("sessionid", sessionId);
        this.minecraftUpdate = new MCUpdate();
    }

    public void init() {
        if (this.applet != null) {
            this.applet.init();
            return;
        }
        this.init(this.getParameter("username"), this.getParameter("sessionid"));
    }

    public void start() {
        if (minecraftUpdateStarted) {
            if (this.applet == null) {
                return;
            }
            this.applet.start();
        } else {
            Thread thread = new Thread(minecraftUpdate) {
                @Override
                public void run() {
                    minecraftUpdate.run();
                    try {
                        if (!minecraftUpdate.fatalError) {
                            replace(minecraftUpdate.createAppletInstance());
                        }
                    } catch (RuntimeException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.setDaemon(true); thread.start();

            thread = new Thread(this::run); thread.setDaemon(true); thread.start();
            this.minecraftUpdateStarted = true;
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

    private void replace(Applet applet) {
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

    private void run() {
        if (this.applet == null) {
            do {
                this.repaint();
            } while (this.applet == null);
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean canPlayOffline() {
        return this.minecraftUpdate.canPlayOffline();
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
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
        if (this.parameters != null) {
            return this.parameters.get(name);
        } else {
            try {
                return super.getParameter(name);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Image getImage() {
        return image;
    }

    public VolatileImage getVolatileImage() {
        return this.volatileImage;
    }

    public Applet getApplet() {
        return this.applet;
    }

    public MCUpdate getMinecraftUpdate() {
        return this.minecraftUpdate;
    }

    public void setVolatileImage(VolatileImage volatileImage) {
        this.volatileImage = volatileImage;
    }
}
