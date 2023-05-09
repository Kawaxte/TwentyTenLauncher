package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth;
import io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthWorker;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import java.util.Objects;
import lombok.val;
import org.json.JSONObject;

public final class MicrosoftAuthUtils {

  private MicrosoftAuthUtils() {}

  public static void executeMicrosoftAuthWorker(String clientId) {
    if (Objects.isNull(clientId)) {
      throw new NullPointerException("clientId cannot be null");
    }
    if (clientId.isEmpty()) {
      throw new IllegalArgumentException("clientId cannot be empty");
    }

    val consumersDeviceCode = MicrosoftAuth.acquireDeviceCode(clientId);
    Objects.requireNonNull(consumersDeviceCode, "consumersDeviceCode cannot be null");
    val deviceCodeResponse = getDeviceCodeResponse(consumersDeviceCode);

    LauncherUtils.addComponentToContainer(
        LauncherPanel.instance,
        new MicrosoftAuthPanel(
            deviceCodeResponse[0], deviceCodeResponse[2], deviceCodeResponse[3]));

    new MicrosoftAuthWorker(
            clientId, deviceCodeResponse[1], deviceCodeResponse[3], deviceCodeResponse[4])
        .execute();
  }

  private static String[] getDeviceCodeResponse(JSONObject object) {
    val userCode = object.getString("user_code");
    val deviceCode = object.getString("device_code");
    val verificationUri = object.getString("verification_uri");
    val expiresIn = String.valueOf(object.getInt("expires_in"));
    val interval = String.valueOf(object.getInt("interval"));
    return new String[] {userCode, deviceCode, verificationUri, expiresIn, interval};
  }
}
