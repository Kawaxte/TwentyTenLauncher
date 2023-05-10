package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.ELanguage;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.VersionGroupBox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public final class LauncherOptionsUtils {

  private static final Logger LOGGER;
  public static Map<String, String> languageLookup;
  public static Map<String, String> versionLookup;

  static {
    LOGGER = LogManager.getLogger(LauncherOptionsUtils.class);
  }

  private LauncherOptionsUtils() {}

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values())
        .forEachOrdered(
            language -> {
              defaultComboBoxModel.addElement(language.getLanguageName());
              languageLookup.put(language.getLanguageName(), language.toString().toLowerCase());
            });

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    languageLookup.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), selectedLanguage))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }

  public static void updateVersionComboBox(VersionGroupBox vgb) {
    versionLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    val fileUrl =
        Optional.ofNullable(
                LauncherOptionsUtils.class.getClassLoader().getResource("versions.json"))
            .orElseThrow(() -> new NullPointerException("fileUrl must not be null"));
    try (val br =
        new BufferedReader(new InputStreamReader(fileUrl.openStream(), StandardCharsets.UTF_8))) {
      val json = new JSONObject(br.lines().collect(Collectors.joining()));
      val versionArrays =
          Collections.unmodifiableList(
              Arrays.asList("legacy_beta", "legacy_alpha", "legacy_infdev"));
      versionArrays.forEach(
          version -> {
            val versionArray = json.getJSONArray(version);
            IntStream.range(0, versionArray.length())
                .mapToObj(versionArray::getJSONObject)
                .sorted(
                    Collections.reverseOrder(
                        Comparator.comparing(
                            object -> {
                              val versionId = object.getString("versionId");
                              val versionIdSplit = versionId.split("_");
                              return versionIdSplit.length == 1
                                  ? versionIdSplit[0]
                                  : new StringBuilder()
                                      .append(versionIdSplit[0])
                                      .append(".")
                                      .append(versionIdSplit[1])
                                      .toString();
                            })))
                .collect(Collectors.toList())
                .forEach(
                    versionObject -> {
                      val showBetaVersionsSelected =
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup.get("showBetaVersionsSelected").toString())
                              && Objects.equals(version, versionArrays.get(0));
                      val showAlphaVersionsSelected =
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup.get("showAlphaVersionsSelected").toString())
                              && Objects.equals(version, versionArrays.get(1));
                      val showInfdevVersionsSelected =
                          Boolean.parseBoolean(
                                  LauncherConfig.lookup
                                      .get("showInfdevVersionsSelected")
                                      .toString())
                              && Objects.equals(version, versionArrays.get(2));
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
      LOGGER.error("Failed to read {}", fileUrl.toString(), ioe);
    }

    val selectedVersion = LauncherConfig.lookup.get("selectedVersion");
    versionLookup.entrySet().stream()
        .filter(entry -> entry.getValue().equals(selectedVersion))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    vgb.getVersionComboBox().setModel(defaultComboBoxModel);
  }

  public static void updateSelectedVersion(VersionGroupBox vgb) {
    String selectedItem = (String) vgb.getVersionComboBox().getSelectedItem();
    selectedItem = versionLookup.get(selectedItem);

    val selectedVersion = LauncherConfig.lookup.get("selectedVersion");
    boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
    if (versionChanged) {
      LauncherConfig.lookup.put("selectedVersion", selectedItem);
      LauncherConfig.saveConfig();
    }
  }

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = languageLookup.get(selectedItem);

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      LauncherConfig.lookup.put("selectedLanguage", selectedItem);
      LauncherConfig.saveConfig();

      val finalSelectedItem = selectedItem;
      SwingUtilities.invokeLater(
          () -> {
            val bundle = LauncherLanguage.getUTF8Bundle(finalSelectedItem);
            if (bundle != null) {
              if (LanguageGroupBox.instance != null) {
                LanguageGroupBox.instance.updateComponentKeyValues(bundle);
              }
              if (OptionsDialog.instance != null) {
                OptionsDialog.instance.updateContainerKeyValues(bundle);
              }
              if (OptionsPanel.instance != null) {
                OptionsPanel.instance.updateComponentKeyValues(bundle);
              }
              if (VersionGroupBox.instance != null) {
                VersionGroupBox.instance.updateComponentKeyValues(bundle);
              }
              if (LauncherOfflinePanel.instance != null) {
                LauncherOfflinePanel.instance.updateComponentKeyValues(bundle);
              }
              if (MicrosoftAuthPanel.instance != null) {
                MicrosoftAuthPanel.instance.updateComponentKeyValues(bundle);
              }
              if (YggdrasilAuthPanel.instance != null) {
                YggdrasilAuthPanel.instance.updateComponentKeyValues(bundle);
              }
            }
          });
    }
  }
}
