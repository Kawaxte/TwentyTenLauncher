/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft;

import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static net.minecraft.MCUtils.OS.osx;

public class MCUpdater implements Runnable {
    static ClassLoader classLoader;
    int state;
    int percentage;
    int currentSizeDownload;
    int totalSizeDownload;
    int currentSizeExtract;
    int totalSizeExtract;
    boolean fatalError;
    static boolean natives_loaded = false;
    static final String assetsUrl = "http://files.betacraft.uk/launcher/assets/";
    static final String clientUrl = "https://piston-data.mojang.com/v1/objects/e1c682219df45ebda589a557aadadd6ed093c86c/";
    static final String clientVersion = "client";
    String fatalErrorDescription;
    String subtaskMessage = "";
    String downloadSpeedMessage = "";
    URL[] urlList;
    Thread thread;

    void init() {
        this.state = 1;
    }

    public void run() {
        this.init();
        this.state = 3;
        this.percentage = 5;
        try {
            this.loadFileURLs();
            String path = AccessController.doPrivileged((PrivilegedExceptionAction<String>) () ->
                    MCUtils.getWorkingDirectory() + File.separator + "bin" + File.separator);

            File dir = new File(path);
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
                if (!mkdirs) {
                    throw new Exception("Failed to create directory " + path);
                }
            }

            if (!this.canPlayOffline()) {
                this.downloadFiles(path);
                this.renameJar(path);
                this.extractZipArchives(path);
            } else {
                this.percentage = 90;
            }
            this.updateClasspath(dir);
            this.state = 7;
        } catch (Exception e) {
            this.fatalErrorOccurred(e.getMessage(), e);
            this.thread = null;
        }
    }

    String getDescriptionForState() {
        switch (this.state) {
            case 1:
                return "Initializing loader";
            case 2:
                return "Determining packages to load";
            case 3:
                return "Checking cache for existing files";
            case 4:
                return "Downloading packages";
            case 5:
                return "Extracting downloaded packages";
            case 6:
                return "Updating classpath";
            case 7:
                return "Done loading";
            default:
                return "Unknown state";
        }
    }

    void loadFileURLs() throws MalformedURLException {
        this.state = 2;

        String libs;
        String natives;
        switch (MCUtils.getPlatform()) {
            case osx:
                libs = "libs-osx.zip";
                natives = "natives-osx.zip";
                break;
            case linux:
                libs = "libs-linux.zip";
                natives = "natives-linux.zip";
                break;
            case windows:
                libs = "libs-windows.zip";
                natives = "natives-windows.zip";
                break;
            default:
                throw new RuntimeException("OS (" + System.getProperty("os.name" + ") not supported."));
        }
        this.urlList = new URL[]{
                new URL(assetsUrl + libs),
                new URL(assetsUrl + natives),
                new URL(clientUrl + clientVersion + ".jar")};
    }

    boolean canPlayOffline() {
        File dir;
        try {
            String path = AccessController.doPrivileged((PrivilegedExceptionAction<String>) () ->
                    MCUtils.getWorkingDirectory() + File.separator + "bin" + File.separator);
            dir = new File(path);
            return dir.exists() && Objects.requireNonNull(dir.listFiles()).length > 0 && new File(path + "minecraft.jar").exists();
        } catch (PrivilegedActionException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void downloadFiles(String path) throws Exception {
        this.state = 4;
        int[] fileSizes = new int[this.urlList.length];

        URLConnection connection;
        for (int i = 0; i < this.urlList.length; i++) {
            connection = this.urlList[i].openConnection();
            connection.setDefaultUseCaches(false);

            fileSizes[i] = connection.getContentLength();
            this.totalSizeDownload += fileSizes[i];
        }
        int initialPercentage = 10;

        byte[] buffer = new byte[1024];
        for (URL url : this.urlList) {
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;

                connection = url.openConnection();
                try (InputStream is = this.getJarInputStream(connection); FileOutputStream fos = new FileOutputStream(path + this.getFileName(url))) {
                    int bufferSize;
                    int downloadedAmount = 0;
                    long downloadStartTime = System.currentTimeMillis();
                    for (bufferSize = is.read(buffer); bufferSize > 0; bufferSize = is.read(buffer)) {
                        fos.write(buffer, 0, bufferSize);
                        this.currentSizeDownload += bufferSize;
                        this.percentage = (int) (((double) this.currentSizeDownload / (double) this.totalSizeDownload) * 45.0D + 10.0D);
                        if (this.percentage > initialPercentage) {
                            initialPercentage = this.percentage;

                            long downloadTime = System.currentTimeMillis() - downloadStartTime;
                            if (downloadTime >= 1000L) {
                                double downloadSpeed = (double) downloadedAmount / downloadTime;
                                downloadSpeed = (downloadSpeed * 100.0D) / 100.0D;
                                this.downloadSpeedMessage = downloadSpeed < 1000.0D ? " @ " + (int) downloadSpeed + " B/sec"
                                        : " @ " + (int) (downloadSpeed / 1000.0D) + " KB/sec";
                                downloadStartTime += 1000L;
                            }
                            this.subtaskMessage = "Retrieving: " + this.getFileName(url) + " "
                                    + this.currentSizeDownload * 100 / this.totalSizeDownload + "%" + this.downloadSpeedMessage;
                        }
                        downloadedAmount += bufferSize;
                    }
                }
            }
        }
        this.subtaskMessage = "";
    }

    void renameJar(String path) {
        File jarFile = new File(path + clientVersion + ".jar");
        File minecraftJar = new File(path + "minecraft.jar");
        boolean renameTo = jarFile.renameTo(minecraftJar);
        if (!renameTo) {
            throw new RuntimeException("Failed to rename " + jarFile.getAbsolutePath() + " to " + minecraftJar.getAbsolutePath());
        }
    }

    void extractZipArchives(String path) {
        String libsZip;
        String nativesZip;
        try {
            File libsDir = new File(path);
            if (!libsDir.exists()) {
                boolean mkdirs = libsDir.mkdirs();
                if (!mkdirs) {
                    throw new RuntimeException("Failed to create directory " + path);
                }
            } else {
                switch (MCUtils.getPlatform()) {
                    case windows:
                        libsZip = "libs-windows.zip";
                        nativesZip = "natives-windows.zip";
                        break;
                    case linux:
                        libsZip = "libs-linux.zip";
                        nativesZip = "natives-linux.zip";
                        break;
                    case osx:
                        libsZip = "libs-osx.zip";
                        nativesZip = "natives-osx.zip";
                        break;
                    default:
                        throw new RuntimeException("OS (" + System.getProperty("os.name" + ") not supported."));
                }
                extractZIP(path + libsZip, String.valueOf(libsDir));
                extractZIP(path + nativesZip, libsDir + File.separator + "natives");
            }
            this.subtaskMessage = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateClasspath(File dir) throws MalformedURLException {
        this.state = 6;
        this.percentage = 95;

        File[] files = dir.listFiles();
        Vector<URL> urls = new Vector<>();
        if (files != null) {
            for (File file : files) {
                if (Stream.of("jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "minecraft.jar").anyMatch(s -> file.getName().contains(s))) {
                    urls.add(file.toURI().toURL());
                }
            }
        }

        URL[] urlArray = new URL[urls.size()];
        urls.copyInto(urlArray);

        if (classLoader == null) {
            classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
                URLClassLoader cl = new URLClassLoader(urlArray, Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(cl);
                return cl;
            });
        } else {
            try {
                Class.forName("net.minecraft.client.Minecraft", true, classLoader);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        String path;
        if (MCUtils.getPlatform() == osx || MCUtils.getPlatform() == MCUtils.OS.linux || MCUtils.getPlatform() == MCUtils.OS.windows) {
            path = dir.getAbsolutePath() + File.separator;
        } else {
            throw new RuntimeException("OS (" + System.getProperty("os.name" + ") not supported."));
        }
        this.unloadNatives(path);
        System.setProperty("org.lwjgl.librarypath", path + "natives");
        System.setProperty("net.java.games.input.librarypath", path + "natives");
        natives_loaded = true;
    }

    void fatalErrorOccurred(String error, Exception e) {
        e.printStackTrace();
        this.fatalError = true;
        this.fatalErrorDescription = "Fatal error occurred (" + this.state + "): " + error;
    }

    void unloadNatives(String nativePath) {
        if (!natives_loaded) {
            return;
        } try {
            Field field = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            field.set(classLoader, new Vector<>());

            Vector<?> loadedLibraryNames = (Vector<?>) field.get(classLoader);
            Enumeration<URL> e = classLoader.getResources("");
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                if ("file".equals(url.getProtocol()) && nativePath.startsWith(url.getFile().replace("%20", " "))) {
                    loadedLibraryNames.remove(url.getFile().replace("%20", " ").substring(nativePath.length()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void extractZIP(String path, String archive) {
        this.state = 5;
        int initialPercentage = this.percentage;

        try (ZipFile zipFile = new ZipFile(path)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            this.totalSizeExtract = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    boolean mkdirs = new File(archive + File.separator + entry.getName()).mkdirs();
                    if (mkdirs) {
                        this.totalSizeExtract += 1;
                    }
                }
                this.totalSizeExtract = (int) ((long) this.totalSizeExtract + entry.getSize());
            }
            this.currentSizeExtract = 0;

            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || entry.getSize() == 0) {
                    continue;
                }

                File file = new File(archive + File.separator + entry.getName());
                boolean mkdirs = file.getParentFile().mkdirs();
                if (mkdirs) {
                    this.percentage = initialPercentage + (int) ((long) (this.totalSizeExtract - this.currentSizeExtract) * (long) (100 - initialPercentage) / (long) this.totalSizeExtract);
                } try (InputStream is = zipFile.getInputStream(entry); FileOutputStream fos = new FileOutputStream(archive + File.separator + entry.getName())) {
                    int bufferSize;
                    byte[] buffer = new byte[1024];
                    while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, bufferSize);
                        this.currentSizeExtract += bufferSize;
                        this.percentage = (int) ((double) this.currentSizeExtract * 20.0D / (double) this.totalSizeExtract) + initialPercentage;
                        this.subtaskMessage = "Extracting: " + entry.getName() + " " + this.currentSizeExtract * 100 / this.totalSizeExtract + "%";
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        boolean delete = new File(path).delete();
        if (!delete) {
            throw new RuntimeException("Failed to delete " + path);
        }
    }

    String getFileName(URL url) {
        if (url.getFile().lastIndexOf('/') != -1) {
            return url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
        }
        return url.getFile();
    }

    InputStream getJarInputStream(URLConnection connection) throws Exception {
        final InputStream[] is = new InputStream[1];
        AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
            is[0] = connection.getInputStream();
            return null;
        });
        if (is[0] == null) {
            throw new Exception("Failed to open " + connection.getURL().toString());
        }
        return is[0];
    }

    Applet createApplet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (Applet) classLoader.loadClass("net.minecraft.client.MinecraftApplet").newInstance();
    }
}