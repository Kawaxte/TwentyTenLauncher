/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.ELanguage;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsPanel;
import io.github.kawaxte.twentyten.launcher.ui.options.VersionGroupBox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public final class LauncherOptionsUtils {

  private static final Logger LOGGER;
  private static Map<String, String> versionMap;
  private static Map<String, String> languageMap;

  static {
    LOGGER = LogManager.getLogger(LauncherOptionsUtils.class);
  }

  private LauncherOptionsUtils() {}

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageMap = new HashMap<>();

    DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>();

    Arrays.stream(ELanguage.values())
        .sorted(Comparator.comparing(ELanguage::getLanguageName))
        .forEachOrdered(
            language -> {
              defaultComboBoxModel.addElement(language.getLanguageName());
              languageMap.put(language.getLanguageName(), language.toString().toLowerCase());
            });

    Object selectedLanguage = LauncherConfig.get(0);
    languageMap.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getValue(), selectedLanguage))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }

  public static void updateVersionComboBox(VersionGroupBox vgb) {
    versionMap = new HashMap<>();

    DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>();

    String fileName = "versions.json";
    URL fileUrl = LauncherOptionsUtils.class.getClassLoader().getResource(fileName);

    InputStream is =
        Optional.ofNullable(
                LauncherOptionsUtils.class.getClassLoader().getResourceAsStream(fileName))
            .orElseThrow(() -> new NullPointerException("is cannot be null"));
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      JSONObject json = new JSONObject(br.lines().collect(Collectors.joining()));
      List<String> types =
          Collections.unmodifiableList(
              Arrays.asList("legacy_beta", "legacy_alpha", "legacy_infdev"));
      types.forEach(
          version -> {
            JSONArray versionArray = json.getJSONArray(version);
            IntStream.range(0, versionArray.length())
                .mapToObj(versionArray::getJSONObject)
                .sorted(
                    (o1, o2) -> {
                      String v1 = o1.getString("versionId");
                      String v2 = o2.getString("versionId");
                      return -compareVersionIds(v1, v2);
                    })
                .collect(Collectors.toList())
                .forEach(
                    o -> {
                      boolean showBetaVersionsSelected =
                          Boolean.parseBoolean(LauncherConfig.get(1).toString())
                              && Objects.equals(version, types.get(0));
                      boolean showAlphaVersionsSelected =
                          Boolean.parseBoolean(LauncherConfig.get(2).toString())
                              && Objects.equals(version, types.get(1));
                      boolean showInfdevVersionsSelected =
                          Boolean.parseBoolean(LauncherConfig.get(3).toString())
                              && Objects.equals(version, types.get(2));
                      if (showBetaVersionsSelected
                          || showAlphaVersionsSelected
                          || showInfdevVersionsSelected) {
                        String versionId = o.getString("versionId");
                        String versionName = o.getString("versionName");

                        versionMap.put(versionName, versionId);
                        defaultComboBoxModel.addElement(versionName);
                      }
                    });
          });
    } catch (IOException ioe) {
      LOGGER.error("Cannot read {}", fileUrl, ioe);
    } finally {
      LOGGER.info("Reading {}", fileUrl);
    }

    Object selectedVersion = LauncherConfig.get(4);
    versionMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(selectedVersion))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    vgb.getVersionComboBox().setModel(defaultComboBoxModel);
  }

  public static void updateSelectedVersion(VersionGroupBox vgb) {
    String selectedItem = (String) vgb.getVersionComboBox().getSelectedItem();
    selectedItem = versionMap.get(selectedItem);

    Object selectedVersion = LauncherConfig.get(4);
    boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
    if (versionChanged) {
      LauncherConfig.set(4, selectedItem);
      LauncherConfig.saveConfig();
    }
  }

  public static void updateSelectedLanguage(LanguageGroupBox lgb) {
    String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
    selectedItem = languageMap.get(selectedItem);

    Object selectedLanguage = LauncherConfig.get(0);
    boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
    if (languageChanged) {
      LauncherConfig.set(0, selectedItem);
      LauncherConfig.saveConfig();

      String finalSelectedItem = selectedItem;

      SwingUtilities.invokeLater(
          () -> {
            UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(finalSelectedItem);
            if (Objects.nonNull(bundle)) {
              LanguageGroupBox.getInstance().updateComponentKeyValues(bundle);
              OptionsDialog.getInstance().updateContainerKeyValues(bundle);
              OptionsPanel.getInstance().updateComponentKeyValues(bundle);
              VersionGroupBox.getInstance().updateComponentKeyValues(bundle);

              if (Objects.nonNull(MicrosoftAuthPanel.getInstance())) {
                MicrosoftAuthPanel.getInstance().updateComponentKeyValues(bundle);
              }
              YggdrasilAuthPanel.getInstance().updateComponentKeyValues(bundle);
            }

            OptionsDialog.getInstance().pack();
          });
    }
  }

  private static int compareVersionIds(String v1, String v2) {
    String[] v1Split = v1.replaceAll("[^\\d._]", "").split("[._]");
    String[] v2Split = v2.replaceAll("[^\\d._]", "").split("[._]");
    int v1SplitLength = v1Split.length;
    int v2SplitLength = v2Split.length;
    int vSplitLength = Math.max(v1SplitLength, v2SplitLength);
    for (int i = 0; i < vSplitLength; i++) {
      int v1SplitValue = i < v1SplitLength ? Integer.parseInt(v1Split[i]) : 0;
      int v2SplitValue = i < v2SplitLength ? Integer.parseInt(v2Split[i]) : 0;
      if (!Objects.equals(v1SplitValue, v2SplitValue)) {
        return Integer.compare(v1SplitValue, v2SplitValue);
      }
    }
    return 0;
  }
}
