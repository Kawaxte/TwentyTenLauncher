package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import io.github.kawaxte.twentyten.util.LauncherLanguageUtils.ELanguage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import lombok.val;
import org.apache.logging.log4j.LogManager;

public class LauncherOptionsUtils {

  private static final List<String> VERSION_TYPES;
  public static Map<String, String> languageLookup;
  public static Map<String, String> versionLookup;
  public static URL versionsFileUrl;

  static {
    LauncherUtils.logger = LogManager.getLogger(LauncherOptionsUtils.class);

    try {
      versionsFileUrl = new URL("https://raw.githubusercontent.com/"
          + "Kawaxte/"
          + "TwentyTenLauncher/"
          + "nightly/"
          + "versions.json");
    } catch (MalformedURLException murle) {
      LauncherUtils.logger.error("Failed to create URL for versions file", murle);
    }

    VERSION_TYPES = Collections.unmodifiableList(Arrays.asList("beta", "alpha", "infdev"));
  }

  private LauncherOptionsUtils() {
  }

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values()).forEachOrdered(language -> {
      defaultComboBoxModel.addElement(language.getName());
      languageLookup.put(language.getName(), language.toString().toLowerCase());
    });
    // TODO: Rewrite this when config is re-implemented
    //    for (Map.Entry<String, String> entry : LanguageUtils.languageMap.entrySet()) {
    //      if (entry.getValue().equals(ConfigUtils.getInstance().getSelectedLanguage())) {
    //        languageModel.setSelectedItem(entry.getKey());
    //        break;
    //      }
    //    }

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }
}
