package net.minecraft;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class MCInstanceGraphics {
    private final MCInstance minecraftInstance;

    public MCInstanceGraphics(MCInstance minecraftInstance) {
        this.minecraftInstance = minecraftInstance;
    }

    protected void paint(Graphics g2) {
        if (minecraftInstance.getApplet() == null) {
            if (minecraftInstance.getVolatileImage() == null
                    || minecraftInstance.getVolatileImage().getWidth() != minecraftInstance.getWidth() / 2
                    || minecraftInstance.getVolatileImage().getHeight() != minecraftInstance.getHeight() / 2) {
                minecraftInstance.setVolatileImage(minecraftInstance.createVolatileImage(
                        minecraftInstance.getWidth() / 2,
                        minecraftInstance.getHeight() / 2));
            }

            Graphics g = minecraftInstance.getVolatileImage().getGraphics();
            int i = 0;
            if (i <= minecraftInstance.getWidth() / 2 / 32) {
                do {
                    int j = 0;
                    while (j <= minecraftInstance.getHeight() / 2 / 32) {
                        g.drawImage(minecraftInstance.getImage(), i * 32, j * 32, null);
                        j++;
                    }
                    i++;
                } while (i <= minecraftInstance.getWidth() / 2 / 32);
            }
            g.setFont(new Font(null, Font.BOLD, 20));
            g.setColor(Color.LIGHT_GRAY);

            String title = "Updating Minecraft";
            if (minecraftInstance.getMinecraftUpdate().fatalError) {
                title = "Failed to launch";
                g.drawString(title,
                        minecraftInstance.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                        minecraftInstance.getHeight() / 2 / 2 - g.getFontMetrics().getHeight() * 2);
            } else {
                g.drawString(title,
                        minecraftInstance.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                        minecraftInstance.getHeight() / 2 / 2 - g.getFontMetrics().getHeight() * 2);
            }
            g.setFont(new Font(null, Font.PLAIN, 12));

            title = minecraftInstance.getMinecraftUpdate().getState();
            if (minecraftInstance.getMinecraftUpdate().fatalError) {
                title = minecraftInstance.getMinecraftUpdate().fatalErrorDescription;
            }
            g.drawString(title,
                    minecraftInstance.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                    minecraftInstance.getHeight() / 2 / 2 + g.getFontMetrics().getHeight());
            title = minecraftInstance.getMinecraftUpdate().subtaskMessage;
            g.drawString(title,
                    minecraftInstance.getWidth() / 2 / 2 - g.getFontMetrics().stringWidth(title) / 2,
                    minecraftInstance.getHeight() / 2 / 2 + g.getFontMetrics().getHeight() * 2);

            if (!minecraftInstance.getMinecraftUpdate().fatalError) {
                g.setColor(Color.BLACK);
                g.fillRect(64, minecraftInstance.getHeight() / 2 - 64,
                        minecraftInstance.getWidth() / 2 - 128 + 1, 5);
                g.setColor(new Color(0, 128, 0));
                g.fillRect(64, minecraftInstance.getHeight() / 2 - 64,
                        minecraftInstance.getMinecraftUpdate().percentage * (minecraftInstance.getWidth() / 2 - 128) / 100, 4);
                g.setColor(new Color(32, 160, 32));
                g.fillRect(64, minecraftInstance.getHeight() / 2 - 64 + 1,
                        minecraftInstance.getMinecraftUpdate().percentage * (minecraftInstance.getWidth() / 2 - 128) / 100 - 2, 1);
            }
            g.dispose();
            g2.drawImage(minecraftInstance.getVolatileImage(), 0, 0,
                    minecraftInstance.getWidth() / 2 * 2, minecraftInstance.getHeight() / 2 * 2, null);
        }
    }

    protected void update(Graphics g) {
        minecraftInstance.paint(g);
    }
}
