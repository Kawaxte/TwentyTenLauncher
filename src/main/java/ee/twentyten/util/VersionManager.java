package ee.twentyten.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VersionManager {

  public static final Map<String, String> FORMATTED_VERSION_IDS;
  public static final Map<String, List<String>> TYPE_TO_IDS;
  public static final Map<String, List<String>> IDS_TO_PORTS;

  static {
    FORMATTED_VERSION_IDS = new HashMap<>();
    FORMATTED_VERSION_IDS.put("beta", "Beta %s");
    FORMATTED_VERSION_IDS.put("alpha", "Alpha v%s");
    FORMATTED_VERSION_IDS.put("infdev", "Infdev (%s)");

    TYPE_TO_IDS = new HashMap<>();
    IDS_TO_PORTS = new HashMap<>();
  }

  private VersionManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getVersionsFile() {
    File workingDirectory = DirectoryManager.getWorkingDirectory();
    File versionsDirectory = new File(workingDirectory, "versions");
    if (!versionsDirectory.exists() && !versionsDirectory.mkdir()) {
      throw new SecurityException("Failed to create versions directory");
    }

    File versionsFile = new File(versionsDirectory, "versions.json");
    if (!versionsFile.exists()) {
      FilesManager.downloadFile(FilesManager.VERSIONS_JSON_URL, versionsFile);
    }
  }
}
