package ee.twentyten.util.launcher.options;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.ui.component.JGroupBox;
import ee.twentyten.lang.ELanguage;
import ee.twentyten.lang.LauncherLanguageImpl;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.ui.launcher.LauncherMicrosoftLoginPanel;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.ui.options.LanguageOptionsGroupBox;
import ee.twentyten.ui.options.OptionsPanel;
import ee.twentyten.ui.options.VersionOptionsGroupBox;
import ee.twentyten.util.config.ConfigUtils;
import java.awt.Container;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

public final class LanguageUtils {

  public static Map<String, String> languageMap;
  @Getter
  @Setter
  private static LauncherLanguageImpl instance;

  static {
    LanguageUtils.setInstance(new LauncherLanguageImpl());
  }

  private LanguageUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static UTF8ResourceBundle getBundle() {
    return LanguageUtils.getInstance().getBundle();
  }

  public static String getString(String key) {
    return LanguageUtils.getInstance().getString(key);
  }

  public static String getString(UTF8ResourceBundle bundle, String key) {
    return LanguageUtils.getInstance().getString(bundle, key);
  }

  public static void setTextToContainer(UTF8ResourceBundle bundle, Container cont, String key) {
    if (cont instanceof JGroupBox) {
      ((JGroupBox) cont).setTitle(LanguageUtils.getString(bundle, key));
    }
    if (cont instanceof JDialog) {
      ((JDialog) cont).setTitle(LanguageUtils.getString(bundle, key));
    }
  }

  public static void setTextToComponent(UTF8ResourceBundle bundle, JComponent c, String key) {
    if (c instanceof JLabel) {
      ((JLabel) c).setText(LanguageUtils.getString(bundle, key));
    }
    if (c instanceof AbstractButton) {
      ((AbstractButton) c).setText(LanguageUtils.getString(bundle, key));
    }
  }

  public static void setTextToComponent(UTF8ResourceBundle bundle, JComponent c, String key,
      Object... args) {
    if (c instanceof JGroupBox) {
      ((JGroupBox) c).setTitle(MessageFormat.format(LanguageUtils.getString(bundle, key), args));
    }
    if (c instanceof JLabel) {
      ((JLabel) c).setText(MessageFormat.format(LanguageUtils.getString(bundle, key), args));
    }
    if (c instanceof AbstractButton) {
      ((AbstractButton) c).setText(
          MessageFormat.format(LanguageUtils.getString(bundle, key), args));
    }
  }

  public static void loadLocale() {
    if (LanguageUtils.getInstance().getBundle() == null) {
      LanguageUtils.getInstance()
          .load("language/locale", ConfigUtils.getInstance().getSelectedLanguage());
    }
  }

  public static void updateLanguageComboBox(LanguageOptionsGroupBox logb) {
    DefaultComboBoxModel<String> languageModel = new DefaultComboBoxModel<>();

    LanguageUtils.languageMap = new HashMap<>();
    for (ELanguage language : ELanguage.values()) {
      String languageName = language.getName();
      String languageValue = language.toString().substring(9);
      languageModel.addElement(languageName);
      LanguageUtils.languageMap.put(languageName, languageValue.toLowerCase());
    }
    for (Map.Entry<String, String> entry : LanguageUtils.languageMap.entrySet()) {
      if (entry.getValue().equals(ConfigUtils.getInstance().getSelectedLanguage())) {
        languageModel.setSelectedItem(entry.getKey());
        break;
      }
    }
    logb.getSetLanguageComboBox().setModel(languageModel);
  }

  public static void updateSelectedLanguage(LanguageOptionsGroupBox logb) {
    String selectedLanguage = (String) logb.getSetLanguageComboBox().getSelectedItem();
    selectedLanguage = LanguageUtils.languageMap.get(selectedLanguage);
    boolean isLanguageChanged = !Objects.equals(selectedLanguage,
        ConfigUtils.getInstance().getSelectedLanguage());
    if (isLanguageChanged) {
      final String finalSelectedLanguage = selectedLanguage;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          UTF8ResourceBundle bundle = UTF8ResourceBundle.getCustomBundle(
              MessageFormat.format("language/locale_{0}", finalSelectedLanguage));
          LanguageUtils.updateLauncherLanguage(bundle);
        }
      });
      ConfigUtils.getInstance().setSelectedLanguage(selectedLanguage);
      ConfigUtils.writeToConfig();
    }
  }

  public static void updateLauncherLanguage(UTF8ResourceBundle bundle) {
    LauncherPanel.getInstance().setTextToComponents(bundle);
    if (LauncherMicrosoftLoginPanel.getInstance() != null) {
      LauncherMicrosoftLoginPanel.getInstance().setTextToComponents(bundle);
    }
    if (LauncherNoNetworkPanel.getInstance() != null) {
      LauncherNoNetworkPanel.getInstance().setTextToComponents(bundle);
    }
    LauncherLoginPanel.getInstance().setTextToComponents(bundle);

    OptionsDialog.getInstance().setTextToContainers(bundle);
    OptionsPanel.getInstance().setTextToComponents(bundle);
    LanguageOptionsGroupBox.getInstance().setTextToContainers(bundle);
    LanguageOptionsGroupBox.getInstance().setTextToComponents(bundle);
    VersionOptionsGroupBox.getInstance().setTextToContainers(bundle);
    VersionOptionsGroupBox.getInstance().setTextToComponents(bundle);

    OptionsDialog.getInstance().pack();
  }
}
