package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.VersionGroupBox;
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

  private static final Logger LOGGER;
  public static Map<String, String> lookup;

  static {
    LOGGER = LogManager.getLogger(LauncherVersionUtils.class);
  }

  private LauncherVersionUtils() {}

  public static void updateVersionComboBox(VersionGroupBox vgb) {
    lookup = new HashMap<>();

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
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup.get("showBetaVersionsSelected").toString())
                              && Objects.equals(version, versions.get(0));
                      val showAlphaVersionsSelected =
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup.get("showAlphaVersionsSelected").toString())
                              && Objects.equals(version, versions.get(1));
                      val showInfdevVersionsSelected =
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup
                                      .get("showInfdevVersionsSelected")
                                      .toString())
                              && Objects.equals(version, versions.get(2));
                      if (showBetaVersionsSelected
                          || showAlphaVersionsSelected
                          || showInfdevVersionsSelected) {
                        val versionId = versionObject.getString("versionId");
                        val versionName = versionObject.getString("versionName");

                        lookup.put(versionName, versionId);
                        defaultComboBoxModel.addElement(versionName);
                      }
                    });
          });
    } catch (IOException ioe) {
      LOGGER.error("Failed to read {}", versionsFileUrl.toString(), ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Failed to parse {} as URI", versionsFileUrl.toString(), urise);
    }

    val selectedVersion = LauncherConfig.lookup.get("selectedVersion");
    lookup.entrySet().stream()
        .filter(entry -> entry.getValue().equals(selectedVersion))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    vgb.getVersionComboBox().setModel(defaultComboBoxModel);
  }
}
