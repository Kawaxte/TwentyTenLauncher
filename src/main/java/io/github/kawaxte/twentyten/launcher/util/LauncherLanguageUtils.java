package io.github.kawaxte.twentyten.launcher.util;

public final class LauncherLanguageUtils {

  private LauncherLanguageUtils() {}

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

  public static String[] getLGBKeys() {
    return new String[] {"lgb.title", "lgb.setLanguageLabel"};
  }

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

  public static String[] getGAWKeys() {
    return new String[] {
      "gaw.updaterStarted",
      "gaw.updaterErrored",
      "gaw.taskStateMessage.error",
      "gaw.taskProgressMessage"
    };
  }

  public static String[] getMAPKeys() {
    return new String[] {"map.copyCodeLabel", "map.openBrowserButton", "map.cancelButton"};
  }

  public static String[] getODKeys() {
    return new String[] {"od.title"};
  }

  public static String[] getOPKeys() {
    return new String[] {
      "op.versionGroupBox", "op.languageGroupBox", "op.openFolderButton", "op.saveOptionsButton"
    };
  }

  public static String[] getVGBKeys() {
    return new String[] {"vgb.title", "vgb.showVersionsCheckBox", "vgb.useVersionLabel"};
  }

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
