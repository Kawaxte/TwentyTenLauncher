package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingWorker;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MicrosoftAuthWorker extends SwingWorker<Object, Void> {

  private final Logger logger;
  private final String clientId;
  private final String deviceCode;
  private final int expiresIn;
  private final int interval;

  {
    this.logger = LogManager.getLogger(this);
  }

  public MicrosoftAuthWorker(
      String clientId, String deviceCode, String expiresIn, String interval) {
    this.clientId = clientId;
    this.deviceCode = deviceCode;
    this.expiresIn = Integer.parseInt(expiresIn);
    this.interval = Integer.parseInt(interval);
  }

  @Override
  protected Object doInBackground() {
    val service = Executors.newSingleThreadScheduledExecutor();
    val future = new Future<?>[1];
    future[0] =
        service.scheduleAtFixedRate(
            new MicrosoftAuthTask(service, clientId, deviceCode),
            0,
            interval * 200L,
            TimeUnit.MILLISECONDS);

    try {
      return future[0].get(expiresIn * 1000L, TimeUnit.MILLISECONDS);
    } catch (ExecutionException ee) {
      this.logger.error("Error while polling for access token", ee.getCause());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      this.logger.error("Interrupted while polling for access token", ie);
    } catch (TimeoutException te) {
      LauncherUtils.addComponentToContainer(
          LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));

      this.logger.error("Timed out while polling for access token", te);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
