/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft;

import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MCUpdater implements Runnable {
    private static ClassLoader classLoader;
    protected URL[] urlList;
    protected Thread thread;
    protected int state;
    public int percentage;
    public int currentSizeDownload;
    public int totalSizeDownload;
    public int currentSizeExtract;
    public int totalSizeExtract;
    public boolean fatalError;
    protected static boolean natives_loaded = false;
    public String fatalErrorDescription;
    protected String assetsUrl = "http://files.betacraft.uk/launcher/assets/";
    protected String clientUrl = "https://piston-data.mojang.com/v1/objects/e1c682219df45ebda589a557aadadd6ed093c86c/";
    protected String clientVersion = "client";
    protected String subtaskMessage = "";

    public void init() {
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
                dir.mkdirs();
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
        } catch (MalformedURLException | PrivilegedActionException e) {
            this.fatalErrorOccurred(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.thread = null;
        }
    }

    protected String getDescriptionForState() {
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

    protected void loadFileURLs() throws MalformedURLException {
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
                throw new RuntimeException("Unknown OS: " + System.getProperty("os.name"));
        }
        this.urlList = new URL[]{new URL(assetsUrl + libs), new URL(assetsUrl + natives), new URL(clientUrl + this.clientVersion + ".jar"),};
    }

    public boolean canPlayOffline() {
        File dir;
        try {
            String path = AccessController.doPrivileged((PrivilegedExceptionAction<String>) () ->
                    MCUtils.getWorkingDirectory() + File.separator + "bin" + File.separator);
            dir = new File(path);
            if (!dir.exists() || Objects.requireNonNull(dir.list()).length == 0) {
                return false;
            }
        } catch (PrivilegedActionException e) {
            this.fatalErrorOccurred("Failed to create working directory", e);
            return false;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals("minecraft.jar")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void downloadFiles(String path) throws Exception {
        this.state = 4;

        int[] fileSizes = new int[this.urlList.length];
        int initialPercentage;
        for (initialPercentage = 0; initialPercentage < this.urlList.length; initialPercentage++) {
            URLConnection connection = this.urlList[initialPercentage].openConnection();
            connection.setDefaultUseCaches(false);

            fileSizes[initialPercentage] = connection.getContentLength();
            totalSizeDownload += fileSizes[initialPercentage];
        }
        initialPercentage = this.percentage = 10;

        byte[] buffer = new byte[1024];
        for (URL url : this.urlList) {
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                URLConnection connection = url.openConnection();
                InputStream is = this.getJarInputStream(connection);

                String currentFile = this.getFileName(url);
                FileOutputStream fos = new FileOutputStream(path + currentFile);

                int bufferSize;
                int downloadedAmount = 0;
                long downloadStartTime = System.currentTimeMillis();
                for (String downloadSpeedMessage = "";
                     (bufferSize = is.read(buffer, 0, buffer.length)) != -1;
                     this.subtaskMessage = this.subtaskMessage + downloadSpeedMessage) {
                    fos.write(buffer, 0, bufferSize);

                    long downloadTime = System.currentTimeMillis() - downloadStartTime;
                    if (downloadTime > 1000L) {
                        long downloadSpeed = downloadedAmount / downloadTime;
                        downloadSpeedMessage = " @ " + downloadSpeed + " KB/s)";
                    }
                    this.currentSizeDownload += bufferSize;
                    this.percentage = initialPercentage + this.currentSizeDownload * 45 / this.totalSizeDownload;
                    this.subtaskMessage = "Retrieving: " + currentFile + " " + this.currentSizeDownload * 100 / this.totalSizeDownload + "%";
                    downloadedAmount += bufferSize;
                }
                is.close();
                fos.close();
            }
        }
        this.subtaskMessage = "";
    }

    protected void renameJar(String path) {
        File jarFile = new File(path + this.clientVersion + ".jar");
        File minecraftJar = new File(path + "minecraft.jar");
        jarFile.renameTo(minecraftJar);
    }

    protected void extractZipArchives(String path) {
        String libsZip;
        String nativesZip;
        try {
            File libsDir = new File(path);
            if (!libsDir.exists()) {
                libsDir.mkdirs();
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
                        throw new RuntimeException("Unknown OS: " + System.getProperty("os.name"));
                }
                extractZIP(path + libsZip, String.valueOf(libsDir));
                extractZIP(path + nativesZip, libsDir + File.separator + "natives");
            }
            this.subtaskMessage = "";
        } catch (Exception e) {
            this.fatalErrorOccurred("Error while extracting archives", e);
        }
    }

    protected void updateClasspath(File dir) throws MalformedURLException {
        this.state = 6;
        this.percentage = 95;

        Vector<URL> urls = new Vector<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains("jinput.jar")
                        || file.getName().contains("lwjgl.jar")
                        || file.getName().contains("lwjgl_util.jar")
                        || file.getName().contains("minecraft.jar")) {
                    urls.add(file.toURI().toURL());
                }
            }
        }

        URL[] urlArray = new URL[urls.size()];
        urls.copyInto(urlArray);

        if (classLoader == null) {
            classLoader = new URLClassLoader(urlArray, MCUpdater.class.getClassLoader());
        } else {
            try {
                classLoader.loadClass("net.minecraft.client.Minecraft");
            } catch (ClassNotFoundException e) {
                this.fatalErrorOccurred("Failed to load Minecraft class", e);
            }
        }

        String path;
        if (!(path = dir.getAbsolutePath()).endsWith(File.separator)) {
            path = path + File.separator;
        }
        this.unloadNatives(path);
        System.setProperty("org.lwjgl.librarypath", path + "natives");
        System.setProperty("net.java.games.input.librarypath", path + "natives");
        natives_loaded = true;
    }

    protected void fatalErrorOccurred(String error, Exception e) {
        e.printStackTrace();
        this.fatalError = true;
        this.fatalErrorDescription = "Fatal error occurred (" + this.state + "): " + error;
    }

    private void unloadNatives(String nativePath) {
        if (natives_loaded) {
            try {
                Field field = ClassLoader.class.getDeclaredField("loadedLibraryNames");
                field.setAccessible(true);

                Vector<?> names = (Vector<?>) field.get(classLoader);
                for (Enumeration<URL> e = classLoader.getResources(""); e.hasMoreElements(); ) {
                    URL url = e.nextElement();
                    if (url.getProtocol().equals("file")) {
                        String file = url.getFile().replace("%20", " ");
                        if (file.startsWith(nativePath)) {
                            names.remove(file.substring(nativePath.length()));
                        }
                    }
                }
            } catch (Exception e) {
                this.fatalErrorOccurred("Error while unloading natives", e);
            }
        }
    }

    protected void extractZIP(String path, String natives) {
        this.state = 5;

        int initialPercentage = this.percentage;
        try {
            ZipFile zipFile = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                String name = entry.getName();
                if (entry.isDirectory()) {
                    new File(natives + File.separator + name).mkdirs();
                } else {
                    totalSizeExtract = (int) ((long) totalSizeExtract + entry.getSize());

                    File file = new File(natives + File.separator + name);
                    file.getParentFile().mkdirs();

                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(file);

                    int bufferSize;
                    for (byte[] buffer = new byte[1024];
                         (bufferSize = is.read(buffer, 0, buffer.length)) != -1;
                         this.subtaskMessage = "Extracting: " + entry.getName() + " " + currentSizeExtract * 100 / totalSizeExtract + "%") {
                        fos.write(buffer, 0, bufferSize);
                        currentSizeExtract += bufferSize;
                        this.percentage = initialPercentage + currentSizeExtract * 20 / totalSizeExtract;
                    }
                    fos.close();
                    is.close();
                }
            }
            zipFile.close();
            new File(path).delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getFileName(URL url) {
        String file = url.getFile();
        if (file.contains("?")) {
            file = file.substring(0, file.indexOf("?"));
        }
        return file.substring(file.lastIndexOf("/") + 1);
    }

    protected InputStream getJarInputStream(URLConnection connection) throws Exception {
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

    public Applet createApplet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (Applet) classLoader.loadClass("net.minecraft.client.MinecraftApplet").newInstance();
    }
}