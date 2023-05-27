package io.github.kawaxte.twentyten.launcher.util;

/**
 * Utility class for retrieving keys from the language resource bundles.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0623_01
 */
public final class LauncherLanguageUtils {

  private LauncherLanguageUtils() {}

  /**
   * Returns an array of keys for {@link io.github.kawaxte.twentyten.launcher.game.EState}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.game.EState
   */
  public static String[] getESEnumKeys() {
    return new String[] {
      "es_enum.initialise",
      "es_enum.checkCache",
      "es_enum.downloadPackages",
      "es_enum.extractPackages",
      "es_enum.updateClasspath",
      "es_enum.done"
    };
  }

  /**
   * Returns an array of keys for {@link
   * io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.options.LanguageGroupBox
   */
  public static String[] getLGBKeys() {
    return new String[] {"lgb.title", "lgb.setLanguageLabel"};
  }

  /**
   * Returns an array of keys for {@link
   * io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel
   */
  public static String[] getLNPPKeys() {
    return new String[] {
      "lnnp.errorLabel.signin",
      "lnnp.errorLabel.signin_null",
      "lnnp.errorLabel.signin_outdated",
      "lnnp.errorLabel.signin_2148916233",
      "lnnp.errorLabel.signin_2148916238",
      "lnnp.playOnlineLabel",
      "lnnp.playOfflineButton",
      "lnnp.retryButton"
    };
  }

  /**
   * Returns an array of keys for {@link io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper
   */
  public static String[] getGAWKeys() {
    return new String[] {
      "gaw.updaterStarted",
      "gaw.updaterErrored",
      "gaw.taskStateMessage.error",
      "gaw.taskProgressMessage"
    };
  }

  /**
   * Returns an array of keys for {@link io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel
   */
  public static String[] getMAPKeys() {
    return new String[] {"map.copyCodeLabel", "map.openBrowserButton", "map.cancelButton"};
  }

  /**
   * Returns an array of keys for {@link
   * io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog
   */
  public static String[] getODKeys() {
    return new String[] {"od.title"};
  }

  /**
   * Returns an array of keys for {@link
   * io.github.kawaxte.twentyten.launcher.ui.options.OptionsPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.options.OptionsPanel
   */
  public static String[] getOPKeys() {
    return new String[] {
      "op.versionGroupBox", "op.languageGroupBox", "op.openFolderButton", "op.saveOptionsButton"
    };
  }

  /**
   * Returns an array of keys for {@link
   * io.github.kawaxte.twentyten.launcher.ui.options.VersionGroupBox}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.options.VersionGroupBox
   */
  public static String[] getVGBKeys() {
    return new String[] {"vgb.title", "vgb.showVersionsCheckBox", "vgb.useVersionLabel"};
  }

  /**
   * Returns an array of keys for {@link io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel
   */
  public static String[] getYAPKeys() {
    return new String[] {
      "yap.microsoftSigninButton",
      "yap.microsoftSigninButton.signing_in",
      "yap.usernameLabel",
      "yap.passwordLabel",
      "yap.optionsButton",
      "yap.rememberPasswordCheckBox",
      "yap.linkLabel",
      "yap.linkLabel.outdated",
      "yap.signinButton",
      "yap.signinButton.signing_in"
    };
  }
}
