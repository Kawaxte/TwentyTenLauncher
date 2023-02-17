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

  public static final String VERSIONS_JSON_URL;
  private static final Map<String, List<String>> IDS_TO_PORTS;
  private static final Map<String, List<String>> TYPES_TO_IDS;
  public static Map<String, String> languages;
  public static Map<String, String> versionIds;
  public static Map<String, String> formattedVersionIds;
  public static String[] versionTypes;

  static {
    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/" + "sojlabjoi/"
        + "AlphacraftLauncher/" + "master/" + "versions.json";

    TYPES_TO_IDS = new HashMap<>();
    IDS_TO_PORTS = new HashMap<>();

    OptionsHelper.versionTypes = new String[]{"beta", "alpha", "infdev"};
    OptionsHelper.formattedVersionIds = new HashMap<>();
    OptionsHelper.formattedVersionIds.put(OptionsHelper.versionTypes[0],
        "Beta %s");
    OptionsHelper.formattedVersionIds.put(OptionsHelper.versionTypes[1],
        "Alpha v%s");
    OptionsHelper.formattedVersionIds.put(OptionsHelper.versionTypes[2],
        "Infdev (%s)");
  }

  private OptionsHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static List<String> getVersionIds(String type) {
    List<String> ids = TYPES_TO_IDS.get(type);
    if (ids == null) {
      File versionsDirectory = FileHelper.createDirectory(
          FileHelper.workingDirectory, "versions");
      File versionsFile = new File(versionsDirectory, "versions.json");

      JSONObject data = FileHelper.readJsonFile(versionsFile);
      Objects.requireNonNull(data, "data == null!");

      JSONArray array = data.getJSONArray(type);
      if (array == null) {
        return Collections.emptyList();
      }

      ids = new ArrayList<>();
      for (int idIndex = 0; idIndex < array.length(); idIndex++) {
        JSONObject obj = array.getJSONObject(idIndex);
        if (obj.has("id")) {
          ids.add(obj.getString("id"));
        }
      }
      TYPES_TO_IDS.put(type, ids);
    }
    return ids;
  }

  public static String getPortsFromIds(String id) throws IOException {
    File versionsDirectory = new File(FileHelper.workingDirectory, "versions");
    File versionsFile = new File(versionsDirectory, "versions.json");

    JSONObject data = FileHelper.readJsonFile(versionsFile);
    Objects.requireNonNull(data, "data == null!");

    for (String type : OptionsHelper.versionTypes) {
      JSONArray array = data.getJSONArray(type);
      for (int versionIndex = 0; versionIndex < array.length();
          versionIndex++) {
        JSONObject version = array.getJSONObject(versionIndex);

        String versionId = version.getString("id");
        String versionPort = version.getString("port");
        IDS_TO_PORTS.put(versionId, Collections.singletonList(versionPort));
      }
    }
    return String.valueOf(IDS_TO_PORTS.get(id));
  }
}
