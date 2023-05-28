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

package ch.kawaxte.launcher.util;

import ch.kawaxte.launcher.ELanguage;
import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.ui.MicrosoftAuthPanel;
import ch.kawaxte.launcher.ui.YggdrasilAuthPanel;
import ch.kawaxte.launcher.ui.options.LanguageGroupBox;
import ch.kawaxte.launcher.ui.options.OptionsDialog;
import ch.kawaxte.launcher.ui.options.OptionsPanel;
import ch.kawaxte.launcher.ui.options.VersionGroupBox;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling everything within (or related to) {@link OptionsDialog}.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @see OptionsDialog
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class LauncherOptionsUtils {

  private static final Logger LOGGER;
  private static Map<String, String> versionMap;
  private static Map<String, String> languageMap;

  static {
    LOGGER = LoggerFactory.getLogger(LauncherOptionsUtils.class);
  }

  private LauncherOptionsUtils() {}

  /**
   * Updates the language combo box in the specified {@link LanguageGroupBox}.
   *
   * <p>It populates the {@link javax.swing.JComboBox} with available languages and sets the
   * currently selected language from the configuration file as the selected item.
   *
   * @param lgb the {@link LanguageGroupBox} that contains the {@link javax.swing.JComboBox} to be
   *     updated
   */
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

  /**
   * Updates the version combo box in the specified {@link VersionGroupBox}.
   *
   * <p>It populates the {@link javax.swing.JComboBox} with available versions retrieved from a JSON
   * resource file and sets the currently selected version from the configuration file as the
   * selected item.
   *
   * @param vgb the {@link VersionGroupBox} that contains the {@link javax.swing.JComboBox} to be
   *     updated
   */
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
    }

    Object selectedVersion = LauncherConfig.get(4);
    versionMap.entrySet().stream()
        .filter(entry -> entry.getValue().equals(selectedVersion))
        .findFirst()
        .ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

    vgb.getVersionComboBox().setModel(defaultComboBoxModel);
  }

  /**
   * Updates the selected version in the configuration, based on the selected item in the {@link
   * javax.swing.JComboBox} of the specified {@link VersionGroupBox}.
   *
   * <p>If the selected version has changed, it saves the updated configuration.
   *
   * @param vgb the {@link VersionGroupBox} that contains the {@link javax.swing.JComboBox} to be
   *     updated
   */
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

  /**
   * Updates the selected language in the configuration, based on the selected item in the {@link
   * javax.swing.JComboBox} of the specified {@link LanguageGroupBox}.
   *
   * @param lgb the {@link LanguageGroupBox} that contains the {@link javax.swing.JComboBox} to be
   *     updated
   */
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
              LanguageGroupBox.getInstance().updateComponentTexts(bundle);
              OptionsDialog.getInstance().updateContainerTitles(bundle);
              OptionsPanel.getInstance().updateComponentTexts(bundle);
              VersionGroupBox.getInstance().updateComponentTexts(bundle);

              if (Objects.nonNull(MicrosoftAuthPanel.getInstance())) {
                MicrosoftAuthPanel.getInstance().updateComponentTexts(bundle);
              }
              YggdrasilAuthPanel.getInstance().updateComponentTexts(bundle);
            }

            OptionsDialog.getInstance().pack();
          });
    }
  }

  /**
   * Helper method for comparing two version IDs lexicographically.
   *
   * <p>This method is used for sorting versions when updating the {@link javax.swing.JComboBox}
   *
   * @param v1 the first {@code versionId} value from the .JSON file
   * @param v2 the second {@code versionId} value from the .JSON file
   * @return a negative integer, zero, or a positive integer as the first {@code versionId} is less
   *     than, equal to, or greater than the second {@code versionId}
   */
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
