/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft;

import java.io.File;
import java.util.Locale;

public final class MCUtils {
    private MCUtils() {
        throw new UnsupportedOperationException();
    }

    public static File getWorkingDirectory() {
        File workingDirectory;
        String userHome = System.getProperty("user.home", ".");
        switch (MCUtils.getPlatform()) {
            case osx:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/");
                break;
            case linux:
                workingDirectory = new File(userHome, ".minecraft");
                break;
            case windows:
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, ".minecraft/");
                    break;
                }
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, ".minecraft/");
        }

        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (osName.contains("mac")) {
            return OS.osx;
        } else if (osName.contains("nix")
                || osName.contains("nux")
                || osName.contains("aix")) {
            return OS.linux;
        } else if (osName.contains("win")) {
            return OS.windows;
        } else {
            throw new RuntimeException("Unknown OS: " + osName);
        }
    }

    public enum OS {
        osx, linux, windows
    }
}