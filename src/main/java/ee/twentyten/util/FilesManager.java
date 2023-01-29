package ee.twentyten.util;

import java.io.File;
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

  public static void downloadFile(String url, String path) {
    try (InputStream is = RequestManager.requestHttpGet(url).getInputStream()) {
      FileUtils.copyInputStreamToFile(is, new File(path));
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't download file from %s to %s", url, path), e);
    }
  }

  public static void moveFile(String srcPath, String destPath) {
    try {
      FileUtils.moveFile(new File(srcPath), new File(destPath));
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Can't move file from %s to %s", srcPath, destPath), e);
    }
  }

  public static void deleteFile(String path) {
    try {
      FileUtils.forceDelete(new File(path));
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't delete file %s", path), e);
    }
  }
}
