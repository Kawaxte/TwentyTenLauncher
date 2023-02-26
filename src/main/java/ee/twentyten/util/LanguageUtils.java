package ee.twentyten.util;

import ee.twentyten.custom.JGroupBox;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.lang.ELanguage;
import ee.twentyten.lang.LauncherLanguageImpl;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.ui.launcher.LauncherMicrosoftLoginPanel;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.ui.options.OptionsLanguageGroupBox;
import ee.twentyten.ui.options.OptionsPanel;
import ee.twentyten.ui.options.OptionsVersionGroupBox;
import java.awt.Container;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import lombok.Getter;
import lombok.Setter;

public final class LanguageUtils {

  public static Map<String, String> languageMap;
  /* LauncherPanel */
  public static String loginFailedKey;
  public static String outdatedLauncherKey;
  public static String noNetworkKey;
  public static String microsoftLoginButtonKey;
  /* LauncherLoginPanel */
  public static String usernameLabelKey;
  public static String passwordLabelKey;
  public static String optionsButtonKey;
  public static String rememberPasswordCheckBoxKey;
  public static String needAccountKey;
  public static String updateLauncherKey;
  public static String loginButtonKey;
  /* LauncherNoNetworkPanel */
  public static String playOnlineLabelKey;
  public static String playOfflineButtonKey;
  public static String tryAgainButtonKey;
  /* LauncherMicrosoftLoginPanel */
  public static String copyUserCodeLabelKey;
  public static String openBrowserButtonKey;
  public static String cancelButtonKey;
  /* OptionsDialog */
  public static String optionsDialogTitleKey;
  /* OptionsPanel */
  public static String openGameDirectoryButtonKey;
  public static String saveOptionsButtonKey;
  /* OptionsLanguageGroupBox */
  public static String optionsLanguageGroupBoxKey;
  public static String setLanguageLabelKey;
  /* OptionsVersionGroupBox */
  public static String optionsVersionGroupBoxKey;
  public static String showVersionsCheckBoxKey;
  public static String useVersionLabelKey;
  @Getter
  @Setter
  private static LauncherLanguageImpl language;

  static {
    LanguageUtils.setLanguage(new LauncherLanguageImpl());
    LanguageUtils.languageMap = new HashMap<>();

    /* UI.Launcher */
    LanguageUtils.getLauncherPanelKeys();
    LanguageUtils.getLauncherLoginPanelKeys();
    LanguageUtils.getLauncherMicrosoftLoginPanelKeys();
    LanguageUtils.getLauncherNoNetworkKeys();

    /* UI.Options */
    LanguageUtils.getOptionsDialogKeys();
    LanguageUtils.getOptionsPanelKeys();
    LanguageUtils.getOptionsLanguageGroupBoxKeys();
    LanguageUtils.getOptionsVersionGroupBoxKeys();
  }

  private LanguageUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static UTF8ResourceBundle getBundle() {
    return LanguageUtils.getLanguage().getBundle();
  }

  public static String getString(String key) {
    return LanguageUtils.getLanguage().getString(key);
  }

  public static String getString(UTF8ResourceBundle bundle, String key) {
    return LanguageUtils.getLanguage().getString(bundle, key);
  }

  public static void setTextToContainer(UTF8ResourceBundle bundle, Container c, String key) {
    if (c instanceof JGroupBox) {
      ((JGroupBox) c).setTitle(LanguageUtils.getString(bundle, key));
    }
    if (c instanceof JDialog) {
      ((JDialog) c).setTitle(LanguageUtils.getString(bundle, key));
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

  private static void getOptionsVersionGroupBoxKeys() {
    LanguageUtils.optionsVersionGroupBoxKey = "ovgb.string.title";
    LanguageUtils.showVersionsCheckBoxKey = "ovgb.checkbox.showVersionsCheckBox";
    LanguageUtils.useVersionLabelKey = "ovgb.label.useVersionLabel";
  }

  private static void getOptionsLanguageGroupBoxKeys() {
    LanguageUtils.optionsLanguageGroupBoxKey = "olgb.string.title";
    LanguageUtils.setLanguageLabelKey = "olgb.label.setLanguageLabel";
  }

  private static void getOptionsPanelKeys() {
    LanguageUtils.openGameDirectoryButtonKey = "op.button.openGameDirectoryButton";
    LanguageUtils.saveOptionsButtonKey = "op.button.saveOptionsButton";
  }

  private static void getOptionsDialogKeys() {
    LanguageUtils.optionsDialogTitleKey = "od.string.title";
  }

  private static void getLauncherNoNetworkKeys() {
    LanguageUtils.playOnlineLabelKey = "lnnp.label.playOnlineLabel";
    LanguageUtils.playOfflineButtonKey = "lnnp.button.playOfflineButton";
    LanguageUtils.tryAgainButtonKey = "lnnp.button.tryAgainButton";
  }

  private static void getLauncherMicrosoftLoginPanelKeys() {
    LanguageUtils.copyUserCodeLabelKey = "lmlp.label.copyUserCodeLabel";
    LanguageUtils.openBrowserButtonKey = "lmlp.button.openBrowserButton";
    LanguageUtils.cancelButtonKey = "lmlp.button.cancelButton";
  }

  private static void getLauncherLoginPanelKeys() {
    LanguageUtils.usernameLabelKey = "llp.label.usernameLabel";
    LanguageUtils.passwordLabelKey = "llp.label.passwordLabel";
    LanguageUtils.optionsButtonKey = "llp.button.optionsButton";
    LanguageUtils.rememberPasswordCheckBoxKey = "llp.checkbox.rememberPasswordCheckBox";
    LanguageUtils.needAccountKey = "llp.label.linkLabel.needAccount";
    LanguageUtils.updateLauncherKey = "llp.label.linkLabel.updateLauncher";
    LanguageUtils.loginButtonKey = "llp.button.loginButton";
  }

  private static void getLauncherPanelKeys() {
    LanguageUtils.loginFailedKey = "lp.label.errorLabel.loginFailed";
    LanguageUtils.outdatedLauncherKey = "lp.label.errorLabel.outdatedLauncher";
    LanguageUtils.noNetworkKey = "lp.label.errorLabel.noNetwork";
    LanguageUtils.microsoftLoginButtonKey = "lp.button.microsoftLoginButton";
  }

  public static void loadLocale() {
    if (LanguageUtils.getLanguage().getBundle() == null) {
      LanguageUtils.getLanguage()
          .load("language/locale", ConfigUtils.getConfig().getSelectedLanguage());
    }
  }

  public static void updateLauncherLanguage(UTF8ResourceBundle bundle) {
    LauncherPanel.getInstance().setTextToComponents(bundle);
    if (LauncherMicrosoftLoginPanel.instance != null) {
      LauncherMicrosoftLoginPanel.getInstance().setTextToComponents(bundle);
    }
    if (LauncherNoNetworkPanel.getInstance() != null) {
      LauncherNoNetworkPanel.getInstance().setTextToComponents(bundle);
    }
    LauncherLoginPanel.getInstance().setTextToComponents(bundle);

    OptionsDialog.getInstance().setTextToContainers(bundle);
    OptionsPanel.getInstance().setTextToComponents(bundle);
    OptionsLanguageGroupBox.getInstance().setTextToContainers(bundle);
    OptionsLanguageGroupBox.getInstance().setTextToComponents(bundle);
    OptionsVersionGroupBox.getInstance().setTextToContainers(bundle);
    OptionsVersionGroupBox.getInstance().setTextToComponents(bundle);

    OptionsDialog.getInstance().pack();
  }

  public static void updateLanguageComboBox(OptionsLanguageGroupBox olgb) {
    DefaultComboBoxModel<String> languageModel = new DefaultComboBoxModel<>();

    LanguageUtils.languageMap = new HashMap<>();
    for (ELanguage language : ELanguage.values()) {
      String languageName = language.getName();
      String languageValue = language.toString().substring(9);
      languageModel.addElement(languageName);
      LanguageUtils.languageMap.put(languageName, languageValue.toLowerCase());
    }
    for (Map.Entry<String, String> entry : LanguageUtils.languageMap.entrySet()) {
      if (entry.getValue().equals(ConfigUtils.getConfig().getSelectedLanguage())) {
        languageModel.setSelectedItem(entry.getKey());
        break;
      }
    }
    olgb.getSetLanguageComboBox().setModel(languageModel);
  }
}
