package net.minecraft.auth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

public class APanelGraphics implements Serializable {
    private final APanel authPanel;

    public APanelGraphics(APanel authPanel) {
        this.authPanel = authPanel;
    }

    public void update(Graphics g) {
        authPanel.paint(g);
    }

    public void paint(Graphics g2) {
        if (authPanel.getVolatileImage() == null
                || authPanel.getVolatileImage().getWidth() != authPanel.getWidth() / 2
                || authPanel.getVolatileImage().getHeight() != authPanel.getHeight() / 2) {
            authPanel.setVolatileImage(authPanel.createVolatileImage(
                    authPanel.getWidth() / 2,
                    authPanel.getHeight() / 2));
        }

        Graphics g = authPanel.getVolatileImage().getGraphics();
        for (int x = 0; x <= authPanel.getWidth() / 2 / 32; x++) {
            for (int y = 0; y <= authPanel.getHeight() / 2 / 32; y++) {
                g.drawImage(authPanel.getImage(), x * 32, y * 32, null);
            }
        }

        String title = "Minecraft Launcher";
        g.setFont(new Font(null, Font.BOLD, 20));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString(title,
                authPanel.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                authPanel.getHeight() / 2 / 2 - g.getFontMetrics().getHeight() * 2);
        g.dispose();
        g2.drawImage(authPanel.getVolatileImage(), 0, 0, authPanel.getWidth() / 2 * 2, authPanel.getHeight() / 2 * 2, null);
    }
}