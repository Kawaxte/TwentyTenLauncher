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
    VERSIONS_JSON_URL = "https://raw.githubusercontent.com/"
        + "sojlabjoi/"
        + "AlphacraftLauncher/"
        + "master/"
        + "versions.json";

    TYPES_TO_IDS = new HashMap<>();
    IDS_TO_PORTS = new HashMap<>();

    OptionsHelper.versionTypes = new String[]
        {
            "beta", "alpha", "infdev"
        };
    OptionsHelper.formattedVersionIds = new HashMap<>();
    OptionsHelper.formattedVersionIds.put(
        OptionsHelper.versionTypes[0], "Beta %s"
    );
    OptionsHelper.formattedVersionIds.put(
        OptionsHelper.versionTypes[1], "Alpha v%s"
    );
    OptionsHelper.formattedVersionIds.put(
        OptionsHelper.versionTypes[2], "Infdev (%s)"
    );
  }

  private OptionsHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static List<String> getVersionIds(
      String type
  ) {

    /* Get the list of ids from the map. */
    List<String> ids = TYPES_TO_IDS.get(type);
    if (ids == null) {

      /* Create the versions directory and the versions file. */
      File versionsDirectory = FileHelper.createDirectory(
          FileHelper.workingDirectory, "versions"
      );
      File versionsFile = new File(
          versionsDirectory, "versions.json"
      );

      /* Read the versions file and return an Optional containing the JSON data. */
      JSONObject data = FileHelper.readJsonFile(versionsFile).get();
      Objects.requireNonNull(
          data, "data == null!"
      );

      /* Get the array of versions by the type. */
      JSONArray array = data.getJSONArray(type);
      if (array == null) {
        return Collections.emptyList();
      }

      /* Create a new list of ids. */
      ids = new ArrayList<>();

      /* Loop through the array of objects and add each of the types'
       * ids to the list of ids. */
      for (int objIndex = 0; objIndex < array.length();
          objIndex++) {
        JSONObject obj = array.getJSONObject(objIndex);
        if (obj.has("id")) {
          ids.add(obj.getString("id"));
        }
      }
      TYPES_TO_IDS.put(type, ids);
    }
    return ids;
  }

  public static String getPortsFromIds(
      String id
  ) throws IOException {

    /* Create the versions directory and the versions file. */
    File versionsDirectory = new File(
        FileHelper.workingDirectory, "versions"
    );
    File versionsFile = new File(
        versionsDirectory, "versions.json"
    );

    /* Read the versions file and return an Optional containing the JSON data. */
    JSONObject data = FileHelper.readJsonFile(versionsFile).get();
    Objects.requireNonNull(
        data, "data == null!"
    );

    /* Loop through the version types. */
    for (String type : OptionsHelper.versionTypes) {

      /* Get the array of versions by the type. */
      JSONArray array = data.getJSONArray(type);

      /* Loop through the array of versions and map each of the types'
       * ids to their respective ports.*/
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
