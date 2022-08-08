package net.minecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MCUpdateDownload {
    private final MCUpdate minecraftUpdate;

    public MCUpdateDownload(MCUpdate minecraftUpdate) {
        this.minecraftUpdate = minecraftUpdate;
    }

    protected void downloadFiles(String path) throws Exception {
        minecraftUpdate.setState(4);
        int[] fileSizes = new int[minecraftUpdate.getUrlList().length];

        URLConnection connection;
        for (int i = 0; i < minecraftUpdate.getUrlList().length; i++) {
            connection = minecraftUpdate.getUrlList()[i].openConnection();
            connection.setDefaultUseCaches(false);

            fileSizes[i] = connection.getContentLength();
            minecraftUpdate.setTotalSizeDownload(minecraftUpdate.getTotalSizeDownload() + fileSizes[i]);
        }

        int initialPercentage = 10;
        byte[] buffer = new byte[1024];
        for (URL url : minecraftUpdate.getUrlList()) {
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                connection = url.openConnection();
                try (InputStream is = minecraftUpdate.getJARInputStream().getJARInputStream(connection); FileOutputStream fos = new FileOutputStream(path + minecraftUpdate.getFileName(url))) {
                    long downloadStartTime = System.currentTimeMillis();
                    int downloadedAmount = 0;
                    for (int bufferSize = is.read(buffer); bufferSize > 0; bufferSize = is.read(buffer)) {
                        fos.write(buffer, 0, bufferSize);
                        minecraftUpdate.setCurrentSizeDownload(minecraftUpdate.getCurrentSizeDownload() + bufferSize);
                        minecraftUpdate.setPercentage((int) ((double) minecraftUpdate.getCurrentSizeDownload() / (double) minecraftUpdate.getTotalSizeDownload() * 45.0D + 10.0D));
                        if (minecraftUpdate.getPercentage() > initialPercentage) {
                            initialPercentage = minecraftUpdate.getPercentage();

                            long downloadTime = System.currentTimeMillis() - downloadStartTime;
                            if (downloadTime >= 1000L) {
                                double downloadSpeed = (double) downloadedAmount / downloadTime;
                                downloadSpeed = (downloadSpeed * 100.0D) / 100.0D;
                                minecraftUpdate.setDownloadSpeedMessage(downloadSpeed < 1000.0D ? " @ " + (int) downloadSpeed + " B/sec" : " @ " + (int) (downloadSpeed / 1000.0D) + " KB/sec");
                                downloadStartTime += 1000L;
                            }
                            minecraftUpdate.setSubtaskMessage("Retrieving: " + minecraftUpdate.getFileName(url) + " "
                                    + minecraftUpdate.getCurrentSizeDownload() * 100 / minecraftUpdate.getTotalSizeDownload() + "%" + minecraftUpdate.getDownloadSpeedMessage());
                        }
                        downloadedAmount += bufferSize;
                    }
                }
            }
        }
        boolean renameTo = new File(path + minecraftUpdate.getClientJAR() + ".jar").renameTo(new File(path + "minecraft.jar"));
        if (!renameTo) {
            throw new Exception("Failed to rename " + path + minecraftUpdate.getClientJAR() + ".jar to " + path + "minecraft.jar");
        }
        minecraftUpdate.setSubtaskMessage("");
    }
}
