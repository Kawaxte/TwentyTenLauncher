package net.minecraft.launcher;

import net.minecraft.MCInstance;

public class LauncherInstances {
    private final LauncherFrame launcherFrame;

    public LauncherInstances(LauncherFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    protected void onlineInstance(String username, String sessionId) {
        try {
            if (username.matches("^\\w+$") && username.length() < 3 || username.length() > 16) {
                username = "Player";
            }
            launcherFrame.setMinecraftInstance(new MCInstance());
            launcherFrame.getMinecraftInstance().parameters.put("username", username);
            launcherFrame.getMinecraftInstance().parameters.put("sessionid", sessionId);
            launcherFrame.getMinecraftInstance().init();
            launcherFrame.removeAll();
            launcherFrame.add(launcherFrame.getMinecraftInstance(), "Center");
            launcherFrame.validate();
            launcherFrame.getMinecraftInstance().start();
            launcherFrame.setAuthPanel(null);
            launcherFrame.setTitle("Minecraft");
        } catch (Exception e) {
            e.printStackTrace();
            launcherFrame.showError(e.toString());
        }
    }

    protected void offlineInstance(String username) {
        try {
            if (username.matches("^\\w+$") && username.length() < 3 || username.length() > 16) {
                username = "Player";
            }
            launcherFrame.setMinecraftInstance(new MCInstance());
            launcherFrame.getMinecraftInstance().parameters.put("username", username);
            launcherFrame.getMinecraftInstance().init();
            launcherFrame.removeAll();
            launcherFrame.add(launcherFrame.getMinecraftInstance(), "Center");
            launcherFrame.validate();
            launcherFrame.getMinecraftInstance().start();
            launcherFrame.setTitle("Minecraft");
        } catch (Exception e) {
            e.printStackTrace();
            launcherFrame.showError(e.toString());
        }
    }
}