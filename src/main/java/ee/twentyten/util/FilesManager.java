package ee.twentyten.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;

public final class FilesManager {

  public static final String VERSIONS_JSON_URL;

  static {
    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/sojlabjoi/AlphacraftLauncher/master/versions.json";
  }

  private FilesManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void downloadFile(String url, File path) {
    try (InputStream is = RequestManager.requestHttpGet(url).getInputStream()) {
      FileUtils.copyInputStreamToFile(is, path);
    } catch (IOException ioe) {
      throw new RuntimeException(String.format("Failed to download file from %s", url), ioe);
    }
  }

  public static void moveFile(File srcPath, File destPath) {
    try {
      FileUtils.moveFile(srcPath, destPath);
    } catch (IOException ioe) {
      try (FileOutputStream fos = new FileOutputStream(destPath)) {
        FileUtils.copyFile(srcPath, fos);

        boolean deleted = srcPath.delete();
        if (!deleted) {
          throw new IOException(
              String.format("Failed to delete file from %s", srcPath.getAbsolutePath()));
        }
      } catch (IOException ioe2) {
        throw new RuntimeException(
            String.format("Failed to move file from %s to %s", srcPath, destPath), ioe2);
      }
    }
  }

  public static void deleteFile(String path) {
    try {
      FileUtils.delete(new File(path));
    } catch (IOException ioe1) {
      try {
        FileUtils.forceDelete(new File(path));
      } catch (IOException ioe2) {
        throw new RuntimeException(String.format("Failed to delete file from %s", path), ioe2);
      }
    }
  }
}
