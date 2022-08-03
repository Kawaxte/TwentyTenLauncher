package net.minecraft.launcher;

import net.minecraft.MCLauncher;

import java.util.ArrayList;

public final class LMain {
    private LMain() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 511L) {
            try {
                String jarPath = MCLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();

                ArrayList<String> argument = new ArrayList<>();
                argument.add("javaw");
                argument.add("-Xmx1G");
                argument.add("-Dsun.java2d.noddraw=true");
                argument.add("-Dsun.java2d.d3d=false");
                argument.add("-Dsun.java2d.opengl=false");
                argument.add("-Dsun.java2d.pmoffscreen=false");
                argument.add("-cp");
                argument.add(jarPath);
                argument.add("net.minecraft.launcher.LFrame");

                ProcessBuilder pb = new ProcessBuilder(argument);
                Process process = pb.start();
                if (process.waitFor() != 0) {
                    throw new Exception("!");
                }
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                LFrame.main(args);
            }
        } else {
            LFrame.main(args);
        }
    }
}