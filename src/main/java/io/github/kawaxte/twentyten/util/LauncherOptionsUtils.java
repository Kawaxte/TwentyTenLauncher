package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle.UTF8Control;
import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import io.github.kawaxte.twentyten.ui.options.LanguageGroupBox;
import java.awt.Container;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import lombok.Getter;
import lombok.val;
import org.apache.logging.log4j.LogManager;

public class LauncherOptionsUtils {

  public static Map<String, String> languageLookup;
  public static Map<String, String> versionLookup;
  public static URL versionsFileUrl;
  private static final List<String> VERSION_TYPES;

  static {
    LauncherUtils.logger = LogManager.getLogger(LauncherOptionsUtils.class);

    try {
      versionsFileUrl = new URL("https://raw.githubusercontent.com/"
          + "Kawaxte/"
          + "TwentyTenLauncher/"
          + "nightly/"
          + "versions.json");
    } catch (MalformedURLException murle) {
      LauncherUtils.logger.error("Failed to create URL for versions file",
          murle);
    }

    VERSION_TYPES = Collections.unmodifiableList(Arrays.asList("beta", "alpha", "infdev"));
  }

  private LauncherOptionsUtils() {
  }

  public static void updateStrings(Container c) {
    val utf8Bundle = (UTF8ResourceBundle) UTF8ResourceBundle.getBundle("messages",
        Locale.forLanguageTag(System.getProperty("user.language")),
        new UTF8Control());

    if (c instanceof JDialog) {
      val dialog = (JDialog) c;
      dialog.setTitle(utf8Bundle.getString(dialog.getTitle()));
    }
    if (c instanceof JGroupBox) {
      val groupBox = (JGroupBox) c;
      groupBox.setTitle(utf8Bundle.getString(groupBox.getTitle()));
    }

    Arrays.stream(c.getComponents()).forEachOrdered(component -> {
      if (component instanceof JLabel) {
        val label = (JLabel) component;
        label.setText(label.getText().matches("<html>.*</html>")
            ? MessageFormat.format("<html><u>{0}</u></html>",
            utf8Bundle.getString(label.getText().replaceAll("<[^>]*>", "")))
            : utf8Bundle.getString(label.getText()));
      }
      if (component instanceof AbstractButton) {
        val abstractButton = (AbstractButton) component;
        abstractButton.setText(utf8Bundle.getString(abstractButton.getText()));
      }
      if (component instanceof Container) {
        val container = (Container) component;
        updateStrings(container);
      }
    });
  }

  public static void updateLanguageComboBox(LanguageGroupBox lgb) {
    languageLookup = new HashMap<>();

    val defaultComboBoxModel = new DefaultComboBoxModel<String>();

    Arrays.stream(ELanguage.values()).forEachOrdered(language -> {
      defaultComboBoxModel.addElement(language.getName());
      languageLookup.put(language.getName(), language.toString().toLowerCase());
    });
    //    for (Map.Entry<String, String> entry : LanguageUtils.languageMap.entrySet()) {
    //      if (entry.getValue().equals(ConfigUtils.getInstance().getSelectedLanguage())) {
    //        languageModel.setSelectedItem(entry.getKey());
    //        break;
    //      }
    //    }

    lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
  }

  public enum ELanguage {
    BG("Български"),
    CS("Čeština"),
    DE("Deutsch"),
    EN("English"),
    ET("Eesti"),
    FI("Suomi"),
    RU("Русский");

    @Getter
    private final String name;

    ELanguage(String name) {
      this.name = name;
    }

    public static ELanguage getLanguage(String s) {
      return Arrays.stream(values()).filter(language -> language.name.equalsIgnoreCase(s))
          .findFirst()
          .orElse(null);
    }
  }
}
