package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.options.VersionGroupBox;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public final class LauncherVersionUtils {

  static final Logger LOGGER;
  public static Map<String, String> versionLookup;

  static {
    LOGGER = LogManager.getLogger(LauncherVersionUtils.class);
  }

  private LauncherVersionUtils() {}

  public static void updateVersionComboBox(VersionGroupBox vgb) {
    versionLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    val versions =
        Collections.unmodifiableList(Arrays.asList("legacy_beta", "legacy_alpha", "legacy_infdev"));
    val versionsFileUrl =
        Optional.ofNullable(
                LauncherVersionUtils.class.getClassLoader().getResource("versions.json"))
            .orElseThrow(() -> new NullPointerException("versionsFileUrl must not be null"));
    try (val br = Files.newBufferedReader(Paths.get(versionsFileUrl.toURI()))) {
      val json = new JSONObject(br.lines().collect(Collectors.joining()));
      versions.forEach(
          version -> {
            val versionArray = json.getJSONArray(version);
            IntStream.range(0, versionArray.length())
                .mapToObj(versionArray::getJSONObject)
                .sorted(
                    (o1, o2) -> o2.getString("versionName").compareTo(o1.getString("versionName")))
                .collect(Collectors.toList())
                .forEach(
                    versionObject -> {
                      val showBetaVersionsSelected =
                          LauncherConfigUtils.CONFIG.isShowBetaVersionsSelected()
                              && Objects.equals(version, versions.get(0));
                      val showAlphaVersionsSelected =
                          LauncherConfigUtils.CONFIG.isShowAlphaVersionsSelected()
                              && Objects.equals(version, versions.get(1));
                      val showInfdevVersionsSelected =
                          LauncherConfigUtils.CONFIG.isShowInfdevVersionsSelected()
                              && Objects.equals(version, versions.get(2));
                      if (showBetaVersionsSelected
                          || showAlphaVersionsSelected
                          || showInfdevVersionsSelected) {
                        val versionId = versionObject.getString("versionId");
                        val versionName = versionObject.getString("versionName");

                        versionLookup.put(versionName, versionId);
                        defaultComboBoxModel.addElement(versionName);
                      }
                    });
          });
    } catch (IOException ioe) {
      LOGGER.error("Failed to read {}", versionsFileUrl.toString(), ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Failed to parse {} as URI", versionsFileUrl.toString(), urise);
    }

    val selectedVersion = LauncherConfigUtils.CONFIG.getSelectedVersion();
    versionLookup.entrySet().stream()
        .filter(entry -> entry.getValue().equals(selectedVersion))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    vgb.getVersionComboBox().setModel(defaultComboBoxModel);
  }
}
