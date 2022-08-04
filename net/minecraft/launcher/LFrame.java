/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.launcher;

import net.minecraft.MCLauncher;
import net.minecraft.auth.APanel;
import net.minecraft.auth.microsoft.MAuthenticate;
import net.minecraft.auth.yggdrasil.YAuthenticate;

import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class LFrame extends Frame {
    private static final long serialVersionUID = 1L;
    private final MAuthenticate microsoftAuthenticate = new MAuthenticate(this);
    private final YAuthenticate yggdrasilAuthenticate = new YAuthenticate(this);
    MCLauncher minecraftLauncher;
    APanel authPanel;

    public LFrame() {
        super("Minecraft Launcher " + LUpdater.currentVersion);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.setMinimumSize(new Dimension(320, 200));
        this.authPanel = new APanel(this);
        this.add(this.authPanel, "Center");
        this.authPanel.setPreferredSize(new Dimension(854, 480));
        this.pack();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                new Thread(() -> {
                    try {
                        Thread.sleep(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("FORCING EXIT!");
                    System.exit(0);
                }).start();
                if (LFrame.this.minecraftLauncher != null) {
                    LFrame.this.minecraftLauncher.stop();
                    LFrame.this.minecraftLauncher.destroy();
                }
                System.exit(0);
            }
        });
        this.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("favicon.png"))).getImage());
    }

    public static void main(String[] args) {
        LFrame launcherFrame = new LFrame();
        launcherFrame.setVisible(true);
    }

    public void playOffline(String username) {
        try {
            if (username.matches("^\\w+$") && username.length() < 3 || username.length() > 16) {
                username = "Player";
            }
            yggdrasilAuthenticate.getLauncherFrame().setMinecraftLauncher(new MCLauncher());
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().customParameters.put("username", username);
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().init();
            yggdrasilAuthenticate.getLauncherFrame().removeAll();
            yggdrasilAuthenticate.getLauncherFrame().add(yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher(), "Center");
            yggdrasilAuthenticate.getLauncherFrame().validate();
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().start();
            yggdrasilAuthenticate.getLauncherFrame().setAuthPanel(null);
            yggdrasilAuthenticate.getLauncherFrame().setTitle("Minecraft");
        } catch (Exception e) {
            e.printStackTrace();
            yggdrasilAuthenticate.getLauncherFrame().getAuthPanel().setError(e.toString());
        }
    }

    public void playOnline(String username, String sessionId) {
        try {
            if (username.matches("^\\w+$") && username.length() < 3 || username.length() > 16) {
                username = "Player";
            }
            yggdrasilAuthenticate.getLauncherFrame().setMinecraftLauncher(new MCLauncher());
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().customParameters.put("username", username);
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().customParameters.put("sessionid", sessionId);
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().init();
            yggdrasilAuthenticate.getLauncherFrame().removeAll();
            yggdrasilAuthenticate.getLauncherFrame().add(yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher(), "Center");
            yggdrasilAuthenticate.getLauncherFrame().validate();
            yggdrasilAuthenticate.getLauncherFrame().getMinecraftLauncher().start();
            yggdrasilAuthenticate.getLauncherFrame().getAuthPanel().getLogin();
            yggdrasilAuthenticate.getLauncherFrame().setAuthPanel(null);
            yggdrasilAuthenticate.getLauncherFrame().setTitle("Minecraft");
        } catch (Exception e) {
            e.printStackTrace();
            yggdrasilAuthenticate.getLauncherFrame().getAuthPanel().setError(e.toString());
        }
    }

    public static boolean canPlayOffline(String username) {
        MCLauncher minecraftLauncher = new MCLauncher();
        minecraftLauncher.init(username, null);
        return minecraftLauncher.canPlayOffline();
    }

    public APanel getAuthPanel() {
        return authPanel;
    }

    public MCLauncher getMinecraftLauncher() {
        return minecraftLauncher;
    }

    public void getMicrosoftAuthenticate() {
        microsoftAuthenticate.authenticate();
    }

    public void getYggdrasilAuthenticate(String username, String password) {
        yggdrasilAuthenticate.authenticate(username, password);
    }

    public void setMinecraftLauncher(MCLauncher minecraftLauncher) {
        this.minecraftLauncher = minecraftLauncher;
    }

    public void setAuthPanel(Object object) {
        this.authPanel = (APanel) object;
    }
}