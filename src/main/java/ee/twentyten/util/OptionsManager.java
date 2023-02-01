package ee.twentyten.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class OptionsManager {

  public static final String[] TYPES;
  public static final Map<String, List<String>> TYPES_TO_IDS;
  public static final Map<String, List<String>> IDS_TO_PORTS;
  public static final Map<String, String> FORMATTED_IDS;
  private static final String VERSIONS_JSON_URL;

  static {
    TYPES = new String[]{"beta", "alpha", "infdev"};
    TYPES_TO_IDS = new HashMap<>();
    IDS_TO_PORTS = new HashMap<>();

    FORMATTED_IDS = new HashMap<>();
    FORMATTED_IDS.put("beta", "Beta %s");
    FORMATTED_IDS.put("alpha", "Alpha v%s");
    FORMATTED_IDS.put("infdev", "Infdev (%s)");

    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/sojlabjoi/AlphacraftLauncher/master/versions.json";
  }

  private OptionsManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getVersionsFile() throws IOException {
    File workingDirectory = LauncherManager.getWorkingDirectory();
    if (!workingDirectory.exists()) {
      LauncherManager.getWorkingDirectory();
    }

    File versionsDirectory = new File(workingDirectory, "versions");
    if (!versionsDirectory.exists()) {
      boolean created = versionsDirectory.mkdirs();
      if (!created) {
        throw new IOException("Failed to create versions directory");
      }
    }

    File versionsFile = new File(versionsDirectory, "versions.json");
    if (!versionsFile.exists()) {
      long lastModified = System.currentTimeMillis() - versionsFile.lastModified();
      if (lastModified > FilesManager.CACHE_EXPIRATION_TIME) {
        FilesManager.downloadFile(VERSIONS_JSON_URL, versionsFile);
      }
    }
  }

  public List<String> getIds(String type) throws IOException {
    List<String> ids = TYPES_TO_IDS.get(type);
    if (ids == null) {
      File versionsDirectory = new File(LauncherManager.getWorkingDirectory(), "versions");
      File versionsFile = new File(versionsDirectory, "versions.json");

      JSONObject data = FilesManager.readJsonFile(versionsFile);
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

  public String getPortsFromIds(String id) throws IOException {
    File versionsDirectory = new File(LauncherManager.getWorkingDirectory(), "versions");
    File versionsFile = new File(versionsDirectory, "versions.json");

    JSONObject data = FilesManager.readJsonFile(versionsFile);
    for (String type : TYPES) {
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
}
