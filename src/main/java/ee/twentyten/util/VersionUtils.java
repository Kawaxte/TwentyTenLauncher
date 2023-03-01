package ee.twentyten.util;

import ee.twentyten.log.ELoggerLevel;
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

public final class VersionUtils {

  public static File versionsDirectory;
  public static Map<String, String> versionMap;
  private static String[] versionTypes;
  private static URL versionsFileUrl;

  static {
    VersionUtils.versionsDirectory = new File(LauncherUtils.workingDirectory, "versions");
    VersionUtils.versionTypes = new String[]{"beta", "alpha", "infdev"};

    try {
      VersionUtils.versionsFileUrl = new URL(
          "https://raw.githubusercontent.com/sojlabjoi/TwentyTenLauncher/stable/versions.json");
    } catch (MalformedURLException murle) {
      LoggerUtils.log("Failed to create URL", murle, ELoggerLevel.ERROR);
    }
  }

  private VersionUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getVersionsFile() {
    File versionsFile = new File(VersionUtils.versionsDirectory, "versions.json");
    if (!VersionUtils.versionsDirectory.exists()) {
      boolean isDirectoryCreated = VersionUtils.versionsDirectory.mkdirs();
      if (!isDirectoryCreated) {
        LoggerUtils.log("Failed to create versions directory", ELoggerLevel.ERROR);
      }
    }
    if (!versionsFile.exists()) {
      FileUtils.downloadFile(VersionUtils.versionsFileUrl, versionsFile);
    }
  }

  public static void updateVersionComboBox(VersionOptionsGroupBox vogb) {
    VersionUtils.versionMap = new HashMap<>();

    DefaultComboBoxModel<String> versionModel = new DefaultComboBoxModel<>();
    for (String versionType : VersionUtils.versionTypes) {
      File versionFile = new File(VersionUtils.versionsDirectory, "versions.json");
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
          if (new File(VersionUtils.versionsDirectory,
              MessageFormat.format("{0}{1}{2}.jar", versionId, File.separator, versionId)).exists()
              && VersionUtils.versionsDirectory.exists() && new File(VersionUtils.versionsDirectory,
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
            VersionUtils.versionMap.put(versionName, versionId);
            versionModel.addElement(versionName);
          }
        }
      }
    }
    String selectedVersion = ConfigUtils.getInstance().getSelectedVersion();
    for (Map.Entry<String, String> entry : VersionUtils.versionMap.entrySet()) {
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
      selectedVersion = VersionUtils.versionMap.get(selectedVersion);
    }
    boolean isVersionChanged = !Objects.equals(selectedVersion,
        ConfigUtils.getInstance().getSelectedVersion());
    if (isVersionChanged) {
      ConfigUtils.getInstance().setSelectedVersion(selectedVersion);
      ConfigUtils.saveConfig();
    }
  }
}
