package net.minecraft;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

public class MCLauncherGraphics implements Serializable {
    private final MCLauncher minecraftLauncher;

    public MCLauncherGraphics(MCLauncher minecraftLauncher) {
        this.minecraftLauncher = minecraftLauncher;
    }

    public void paint(Graphics g2) {
        if (minecraftLauncher.getApplet() == null) {
            if (minecraftLauncher.getVolatileImage() == null
                    || minecraftLauncher.getVolatileImage().getWidth() != minecraftLauncher.getWidth() / 2
                    || minecraftLauncher.getVolatileImage().getHeight() != minecraftLauncher.getHeight() / 2) {
                minecraftLauncher.setVolatileImage(minecraftLauncher.createVolatileImage(
                        minecraftLauncher.getWidth() / 2,
                        minecraftLauncher.getHeight() / 2));
            }

            Graphics g = minecraftLauncher.getVolatileImage().getGraphics();
            for (int i = 0; i <= minecraftLauncher.getWidth() / 2 / 32; i++) {
                for (int j = 0; j <= minecraftLauncher.getHeight() / 2 / 32; j++) {
                    g.drawImage(minecraftLauncher.getImage(), i * 32, j * 32, null);
                }
            }

            String title = "Updating Minecraft";
            g.setFont(new Font(null, Font.BOLD, 20));
            g.setColor(Color.LIGHT_GRAY);

            if (minecraftLauncher.getMinecraftUpdater().fatalError) {
                title = "Failed to launch";
            }
            g.drawString(title,
                    minecraftLauncher.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                    minecraftLauncher.getHeight() / 2 / 2 - g.getFontMetrics().getHeight() * 2);
            g.setFont(new Font(null, Font.PLAIN, 12));

            title = minecraftLauncher.getMinecraftUpdater().getDescriptionForState();
            if (minecraftLauncher.getMinecraftUpdater().fatalError) {
                title = minecraftLauncher.getMinecraftUpdater().fatalErrorDescription;
            }
            g.drawString(title,
                    minecraftLauncher.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                    minecraftLauncher.getHeight() / 2 / 2 + g.getFontMetrics().getHeight());

            title = minecraftLauncher.getMinecraftUpdater().subtaskMessage;
            g.drawString(title,
                    minecraftLauncher.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                    minecraftLauncher.getHeight() / 2 / 2 + g.getFontMetrics().getHeight() * 2);

            if (!minecraftLauncher.getMinecraftUpdater().fatalError) {
                g.setColor(Color.BLACK);
                g.fillRect(64, minecraftLauncher.getHeight() / 2 - 64,
                        minecraftLauncher.getWidth() / 2 - 128 + 1, 5);
                g.setColor(new Color(0, 128, 0));
                g.fillRect(64, minecraftLauncher.getHeight() / 2 - 64,
                        minecraftLauncher.getMinecraftUpdater().percentage * (minecraftLauncher.getWidth() / 2 - 128) / 100, 4);
                g.setColor(new Color(32, 160, 32));
                g.fillRect(64, minecraftLauncher.getHeight() / 2 - 64 + 1,
                        minecraftLauncher.getMinecraftUpdater().percentage * (minecraftLauncher.getWidth() / 2 - 128) / 100 - 2, 1);
            }
            g.dispose();
            g2.drawImage(minecraftLauncher.getVolatileImage(), 0, 0,
                    minecraftLauncher.getWidth() / 2 * 2, minecraftLauncher.getHeight() / 2 * 2, null);
        }
    }

    public void update(Graphics g) {
        minecraftLauncher.paint(g);
    }
}