package net.minecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MCUpdateExtract {
    private final MCUpdate minecraftUpdate;

    public MCUpdateExtract(MCUpdate minecraftUpdate) {
        this.minecraftUpdate = minecraftUpdate;
    }

    protected void extractZIPArchives(String path) {
        try {
            File libsDir = new File(path);
            if (!libsDir.exists()) {
                boolean mkdirs = libsDir.mkdirs();
                assert mkdirs : "Failed to create libs directory in " + path;
                return;
            }

            String libsZip;
            String nativesZip;
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
                    throw new RuntimeException("OS (" + System.getProperty("os.name") + ") not supported");
            }
            extractZIP(path + libsZip, String.valueOf(libsDir));
            extractZIP(path + nativesZip, libsDir + File.separator + "natives");
            minecraftUpdate.setSubtaskMessage("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractZIP(String path, String archive) throws RuntimeException, IOException {
        minecraftUpdate.setState(5);
        int initialPercentage = minecraftUpdate.getPercentage();
        try (ZipFile zipFile = new ZipFile(path)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries.hasMoreElements()) {
                int totalSizeExtract = 0;
                do {
                    ZipEntry entry = entries.nextElement();
                    if (entry.isDirectory()) {
                        boolean mkdirs = new File(archive + File.separator + entry.getName()).mkdirs();
                        if (mkdirs) {
                            totalSizeExtract++;
                        }
                    }
                    totalSizeExtract = (int) ((long) totalSizeExtract + entry.getSize());
                } while (entries.hasMoreElements());
                entries = zipFile.entries();
                if (entries.hasMoreElements()) {
                    int currentSizeExtract = 0;
                    do {
                        ZipEntry entry = entries.nextElement();
                        if (entry.isDirectory() || entry.getSize() == 0) {
                            continue;
                        }

                        File file = new File(archive + File.separator + entry.getName());
                        boolean mkdirs = file.getParentFile().mkdirs();
                        if (mkdirs) {
                            minecraftUpdate.setPercentage(initialPercentage + (int) ((long) (totalSizeExtract - currentSizeExtract) * (long) (100 - initialPercentage) / (long) totalSizeExtract));
                        }
                        try (InputStream is = zipFile.getInputStream(entry); FileOutputStream fos = new FileOutputStream(archive + File.separator + entry.getName())) {
                            int bufferSize;
                            byte[] buffer = new byte[1024];
                            if ((bufferSize = is.read(buffer, 0, buffer.length)) != -1) {
                                do {
                                    fos.write(buffer, 0, bufferSize);
                                    currentSizeExtract += bufferSize;
                                    minecraftUpdate.setPercentage((int) ((double) currentSizeExtract * 20.0D / (double) totalSizeExtract) + initialPercentage);
                                    minecraftUpdate.setSubtaskMessage(String.format("Extracting: %s %d%%", entry.getName(), currentSizeExtract * 100 / totalSizeExtract));
                                } while ((bufferSize = is.read(buffer, 0, buffer.length)) != -1);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to extract " + entry.getName() + " from " + path, e);
                        }
                    } while (entries.hasMoreElements());
                }
            }
        }
        boolean delete = new File(path).delete();
        assert delete : "Failed to delete " + path;
    }
}
