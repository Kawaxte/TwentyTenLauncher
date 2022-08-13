/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.launcher;

import net.minecraft.MCInstance;
import net.minecraft.MCUtils;
import net.minecraft.launcher.auth.AuthPanel;
import net.minecraft.launcher.auth.microsoft.MSAuthenticate;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LauncherFrame extends Frame {
    private static final long serialVersionUID = 1L;
    private final LauncherInstances launcherInstances = new LauncherInstances(this);
    private final MSAuthenticate microsoftAuthenticate = new MSAuthenticate(this);
    public MCInstance minecraftInstance;
    public AuthPanel authPanel;

    public LauncherFrame() {
        super("Minecraft Launcher " + LauncherUpdate.currentVersion);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.setMinimumSize(new Dimension(320, 200));
        this.authPanel = new AuthPanel(this);
        this.add(this.authPanel, "Center");
        this.authPanel.setPreferredSize(new Dimension(854, 480));
        this.pack();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            private void run() {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("FORCING EXIT!");
                System.exit(0);
            }

            @Override
            public void windowClosing(WindowEvent we) {
                new Thread(this::run).start();
                if (LauncherFrame.this.minecraftInstance != null) {
                    LauncherFrame.this.minecraftInstance.stop();
                    LauncherFrame.this.minecraftInstance.destroy();
                }
                System.exit(0);
            }
        });
        try {
            this.setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("resources/favicon.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (!MCUtils.isJavaFXInstalled()) {
            JOptionPane.showMessageDialog(null, "JavaFX is not installed. Please install JavaFX to use this feature.");
            System.exit(1);
            return;
        }
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 511L) {
            try {
                ArrayList<String> parameters = new ArrayList<>();
                parameters.add(MCUtils.getPlatform() == MCUtils.OS.windows ? "javaw" : "java");
                parameters.add("-Xmx1G");
                parameters.add("-Dsun.java2d.noddraw=true");
                parameters.add("-Dsun.java2d.d3d=false");
                parameters.add("-Dsun.java2d.opengl=false");
                parameters.add("-Dsun.java2d.pmoffscreen=false");
                parameters.add("-cp");
                parameters.add(MCInstance.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                parameters.add("net.minecraft.launcher.LauncherFrame");

                ProcessBuilder pb = new ProcessBuilder(parameters);
                Process process = pb.start();
                if (process.waitFor() != 0) {
                    throw new Exception("!");
                }
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                LauncherFrame launcherFrame = new LauncherFrame();
                launcherFrame.setVisible(true);
            }
        } else {
            LauncherFrame launcherFrame = new LauncherFrame();
            launcherFrame.setVisible(true);
        }
    }

    public void showError(String error) {
        this.removeAll();
        this.add(this.authPanel);
        this.authPanel.setError(error);
        this.validate();
    }

    public static boolean canPlayOffline(String username) {
        MCInstance minecraftLauncher = new MCInstance();
        minecraftLauncher.init(username, null);
        return minecraftLauncher.canPlayOffline();
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public MSAuthenticate getMicrosoftAuthenticate() {
        return this.microsoftAuthenticate;
    }

    public MCInstance getMinecraftInstance() {
        return minecraftInstance;
    }

    public AuthPanel getAuthPanel() {
        return authPanel;
    }

    public void getOnlineInstance(String username, String sessionId) {
        launcherInstances.onlineInstance(username, sessionId);
    }

    public void getOfflineInstance(String username) {
        launcherInstances.offlineInstance(username);
    }

    public void setMinecraftInstance(MCInstance minecraftInstance) {
        this.minecraftInstance = minecraftInstance;
    }

    public void setAuthPanel(Object o) {
        this.authPanel = (AuthPanel) o;
    }
}
