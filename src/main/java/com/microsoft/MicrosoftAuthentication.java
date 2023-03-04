package com.microsoft;

import ee.twentyten.log.ELevel;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import lombok.Getter;
import org.json.JSONObject;

abstract class MicrosoftAuthentication {

  @Getter
  String clientId;
  String accessToken;

  {
    this.clientId = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
  }

  public void handleXstsTokenErrors(JSONObject result) {
    long xErr = result.getLong("XErr");
    switch ((int) xErr) {
      case (int) 2148916233L:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.2148916233"));
        LoggerUtils.logMessage("Xbox Live not linked to Microsoft account", ELevel.ERROR);
        break;
      case (int) 2148916235L:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.2148916235"));
        LoggerUtils.logMessage("Xbox Live not available in this region", ELevel.ERROR);
        break;
      case (int) 2148916236L:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.2148916236"));
        LoggerUtils.logMessage("Adult verification required in this region", ELevel.ERROR);
        break;
      case (int) 2148916237L:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.2148916237"));
        LoggerUtils.logMessage("Age verification required in this region", ELevel.ERROR);
        break;
      case (int) 2148916238L:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.2148916238"));
        LoggerUtils.logMessage("Required to be part of a family account", ELevel.ERROR);
        break;
    }
  }

  void handleDeviceCodeErrors(final JSONObject result, final AtomicBoolean isExpired,
      final Future<?>[] pollingTask) {
    String errorUri = result.getString("error_uri");
    String error = result.getString("error");
    switch (error) {
      case "authorization_pending":
        break;
      case "expired_token":
        isExpired.set(true);
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.expiredDeviceCode"));
        pollingTask[0].cancel(true);
        break;
      default:
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), error);
        LoggerUtils.logMessage(errorUri, ELevel.ERROR);
        pollingTask[0].cancel(true);
        break;
    }
  }

  void startProgressBar(final JProgressBar progressBar, final boolean isAuthorised) {
    final Timer progressBarTimer = new Timer(1000, null);
    progressBarTimer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == progressBarTimer) {
          progressBar.setValue(progressBar.getValue() - 1);
          if (progressBar.getValue() == 0 || isAuthorised) {
            progressBarTimer.stop();
          }
        }
      }
    });
    progressBarTimer.setInitialDelay(0);
    progressBarTimer.setRepeats(false);
    progressBarTimer.start();
  }

  public abstract void authenticate();

  public abstract JSONObject pollForAccessToken(String deviceCode, int expiresIn, int interval);

  public abstract JSONObject acquireUserCode(String clientId);

  public abstract JSONObject acquireAccessToken(String clientId, String deviceCode);

  public abstract JSONObject acquireXblToken(String accessToken);

  public abstract JSONObject acquireXstsToken(String xblToken);

  public abstract JSONObject acquireMinecraftToken(String uhs, String xstsToken);

  public abstract JSONObject acquireMinecraftStore(String minecraftToken);

  public abstract JSONObject acquireMinecraftProfile(String minecraftToken);
}
