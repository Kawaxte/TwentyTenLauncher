package ee.twentyten.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

public final class OptionsHelper {

  private static final Map<String, List<String>> IDS_TO_PORTS;
  private static final Map<String, List<String>> TYPES_TO_IDS;
  private static final String VERSIONS_JSON_URL;
  public static Map<String, String> versionIds;
  public static Map<String, String> formattedVersionIds;
  public static String[] versionTypes;

  static {
    TYPES_TO_IDS = new HashMap<>();
    IDS_TO_PORTS = new HashMap<>();

    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/sojlabjoi/AlphacraftLauncher/master/versions.json";
    
    versionTypes = new String[]{"beta", "alpha", "infdev"};
    formattedVersionIds = new HashMap<>();
    formattedVersionIds.put(versionTypes[0], "Beta %s");
    formattedVersionIds.put(versionTypes[1], "Alpha v%s");
    formattedVersionIds.put(versionTypes[2], "Infdev (%s)");
  }

  private OptionsHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static List<String> getVersionIds(String type) {
    List<String> ids = TYPES_TO_IDS.get(type);
    if (ids == null) {
      File versionsDirectory = FileHelper.createDirectory(FileHelper.workingDirectory, "versions");
      File versionsFile = new File(versionsDirectory, "versions.json");

      JSONObject data = FileHelper.readJsonFile(versionsFile);
      JSONArray array = data.getJSONArray(type);
      if (array == null) {
        return Collections.emptyList();
      }

      ids = new ArrayList<>();
      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        if (obj.has("id")) {
          ids.add(obj.getString("id"));
        }
      }
      TYPES_TO_IDS.put(type, ids);
    }
    return ids;
  }

  public static String getPortsFromIds(String id) throws IOException {
    File versionsDirectory = new File(LauncherHelper.getWorkingDirectory(), "versions");
    File versionsFile = new File(versionsDirectory, "versions.json");

    JSONObject data = FileHelper.readJsonFile(versionsFile);
    for (String type : versionTypes) {
      JSONArray array = data.getJSONArray(type);
      for (int i = 0; i < array.length(); i++) {
        JSONObject version = array.getJSONObject(i);

        String versionId = version.getString("id");
        String versionPort = version.getString("port");
        IDS_TO_PORTS.put(versionId, Collections.singletonList(versionPort));
      }
    }
    return String.valueOf(IDS_TO_PORTS.get(id));
  }

  public static void downloadVersionsFile() throws IOException {
    File workingDirectory = LauncherHelper.getWorkingDirectory();
    Objects.requireNonNull(workingDirectory, "workingDirectory == null!");
    if (!workingDirectory.exists()) {
      LauncherHelper.getWorkingDirectory();
    }

    File versionsDirectory = new File(workingDirectory, "versions");
    if (!versionsDirectory.exists()) {
      boolean created = versionsDirectory.mkdirs();
      if (!created) {
        LogHelper.logError(OptionsHelper.class, "Failed to create versions directory");
      }
    }

    File versionsFile = new File(versionsDirectory, "versions.json");
    if (!versionsFile.exists()) {
      long lastModified = System.currentTimeMillis() - versionsFile.lastModified();
      if (lastModified > FileHelper.CACHE_EXPIRATION_TIME) {
        FileHelper.downloadFile(VERSIONS_JSON_URL, versionsFile);
      }
    }
  }
}
