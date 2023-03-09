package ee.twentyten.util;

import ee.twentyten.log.ELevel;
import ee.twentyten.ui.options.VersionOptionsGroupBox;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import org.json.JSONArray;
import org.json.JSONObject;

public final class OptionsUtils {

  public static File versionsDirectory;
  public static Map<String, String> versionMap;
  private static String[] versionTypes;
  private static URL versionsFileUrl;

  static {
    OptionsUtils.versionsDirectory = new File(LauncherUtils.workingDirectory, "versions");
    OptionsUtils.versionTypes = new String[]{"beta", "alpha", "infdev"};

    try {
      OptionsUtils.versionsFileUrl = new URL(
          "https://raw.githubusercontent.com/sojlabjoi/TwentyTenLauncher/stable/versions.json");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private OptionsUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getVersionsFile() {
    File versionsFile = new File(OptionsUtils.versionsDirectory, "versions.json");
    if (!OptionsUtils.versionsDirectory.exists()) {
      boolean isDirectoryCreated = OptionsUtils.versionsDirectory.mkdirs();
      if (!isDirectoryCreated) {
        LoggerUtils.logMessage("Failed to create versions directory", ELevel.ERROR);
      }
    }
    if (!versionsFile.exists()) {
      FileUtils.downloadFile(OptionsUtils.versionsFileUrl, versionsFile);
    }
  }

  public static int getProxyPort(String version) {
    File versionsFile = new File(OptionsUtils.versionsDirectory, "versions.json");
    JSONObject versionJson = FileUtils.readJsonFile(versionsFile);

    String versionType = OptionsUtils.getVersionType(version);
    for (String versionTypeKey : versionJson.keySet()) {
      if (versionTypeKey.equals(versionType)) {
        JSONArray versionArray = versionJson.getJSONArray(versionTypeKey);
        return OptionsUtils.getProxyPortFromVersionArray(versionArray, version);
      }
    }
    return Integer.parseInt("80");
  }

  private static String getVersionType(String version) {
    switch (version.charAt(0)) {
      case 'b':
        return "beta";
      case 'a':
        return "alpha";
      case 'i':
        return "infdev";
      default:
        return null;
    }
  }

  private static int getProxyPortFromVersionArray(JSONArray array, String version) {
    for (int i = 0; i < array.length(); i++) {
      JSONObject object = array.getJSONObject(i);
      if (object.getString("version_id").equals(version)) {
        return object.getInt("proxy_port");
      }
    }
    return Integer.parseInt("80");
  }

  public static void updateVersionComboBox(VersionOptionsGroupBox vogb) {
    OptionsUtils.versionMap = new HashMap<>();

    DefaultComboBoxModel<String> versionModel = new DefaultComboBoxModel<>();
    for (String versionType : OptionsUtils.versionTypes) {
      File versionFile = new File(OptionsUtils.versionsDirectory, "versions.json");
      if (versionFile.exists()) {
        JSONObject versionsJson = FileUtils.readJsonFile(versionFile);
        JSONArray versionsArray = versionsJson.getJSONArray(versionType);
        for (int i = versionsArray.length() - 1; i >= 0; i--) {
          JSONObject versionJson = versionsArray.getJSONObject(i);

          String versionId = versionJson.getString("version_id");
          String versionName =
              Objects.equals(versionType, "beta") ? MessageFormat.format("Beta {0}",
                  versionId.substring(1))
                  : Objects.equals(versionType, "alpha") ? MessageFormat.format("Alpha v{0}",
                      versionId.substring(1))
                      : MessageFormat.format("Infdev {0}", versionId.substring(3));
          if (new File(OptionsUtils.versionsDirectory,
              MessageFormat.format("{0}{1}{2}.jar", versionId, File.separator, versionId)).exists()
              && OptionsUtils.versionsDirectory.exists() && new File(OptionsUtils.versionsDirectory,
              versionId).exists()) {
            versionName = MessageFormat.format("<html><b>{0}</b></html>", versionName);
          }

          boolean isBetaVersionSelected =
              ConfigUtils.getInstance().isShowBetaVersionsSelected() && Objects.equals(versionType,
                  "beta");
          boolean isAlphaVersionSelected =
              ConfigUtils.getInstance().isShowAlphaVersionsSelected() && Objects.equals(versionType,
                  "alpha");
          boolean isInfdevVersionSelected =
              ConfigUtils.getInstance().isShowInfdevVersionsSelected() && Objects.equals(
                  versionType, "infdev");
          if (isBetaVersionSelected || isAlphaVersionSelected || isInfdevVersionSelected) {
            OptionsUtils.versionMap.put(versionName, versionId);
            versionModel.addElement(versionName);
          }
        }
      }
    }
    String selectedVersion = ConfigUtils.getInstance().getSelectedVersion();
    for (Map.Entry<String, String> entry : OptionsUtils.versionMap.entrySet()) {
      if (Objects.equals(entry.getValue(), selectedVersion)) {
        versionModel.setSelectedItem(entry.getKey());
        break;
      }
    }
    vogb.getUseVersionComboBox().setModel(versionModel);
  }

  public static void updateSelectedVersion(VersionOptionsGroupBox vogb) {
    String selectedVersion = (String) vogb.getUseVersionComboBox().getSelectedItem();
    if (selectedVersion != null) {
      selectedVersion = OptionsUtils.versionMap.get(selectedVersion);
    }
    boolean isVersionChanged = !Objects.equals(selectedVersion,
        ConfigUtils.getInstance().getSelectedVersion());
    if (isVersionChanged) {
      ConfigUtils.getInstance().setSelectedVersion(selectedVersion);
      ConfigUtils.writeToConfig();
    }
  }
}
