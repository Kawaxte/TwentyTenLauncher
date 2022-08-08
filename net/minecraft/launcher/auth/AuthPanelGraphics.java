package net.minecraft.launcher.auth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class AuthPanelGraphics {
    private final AuthPanel authPanel;

    public AuthPanelGraphics(AuthPanel authPanel) {
        this.authPanel = authPanel;
    }

    protected void paint(Graphics g2) {
        if (authPanel.getVolatileImage() == null
                || authPanel.getVolatileImage().getWidth() != authPanel.getWidth() / 2
                || authPanel.getVolatileImage().getHeight() != authPanel.getHeight() / 2) {
            authPanel.setVolatileImage(authPanel.createVolatileImage(
                    authPanel.getWidth() / 2,
                    authPanel.getHeight() / 2));
        }

        Graphics g = authPanel.getVolatileImage().getGraphics();
        int i = 0;
        if (i <= authPanel.getWidth() / 2 / 32) {
            do {
                int j = 0;
                while (j <= authPanel.getHeight() / 2 / 32) {
                    g.drawImage(authPanel.getImage(), i * 32, j * 32, null);
                    j++;
                }
                i++;
            } while (i <= authPanel.getWidth() / 2 / 32);
        }
        g.setFont(new Font(null, Font.BOLD, 20));
        g.setColor(Color.LIGHT_GRAY);

        String title = "Minecraft Launcher";
        g.drawString(title,
                authPanel.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                authPanel.getHeight() / 2 / 2 - g.getFontMetrics().getHeight() * 2);
        g.dispose();
        g2.drawImage(authPanel.getVolatileImage(), 0, 0, authPanel.getWidth() / 2 * 2, authPanel.getHeight() / 2 * 2, null);
    }

    protected void update(Graphics g) {
        authPanel.paint(g);
    }
}
