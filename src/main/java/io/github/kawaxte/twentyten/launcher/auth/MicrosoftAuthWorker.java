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

package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class schedules a {@link io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthTask} at a
 * fixed rate. The task is responsible for polling the device code, which is part of the OAuth 2.0
 * authorisation grant.
 *
 * <p>The polling process continues for a certain period of time (specified by 'expiresIn') or until
 * it gets interrupted. If the process gets interrupted or encounters an exception, appropriate
 * error messages are logged.
 *
 * @see javax.swing.SwingWorker
 * @author Kawaxte
 * @since 1.5.0823_02
 * @see <a
 *     href="https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-device-code">
 *     OAuth 2.0 device authorization grant</a>
 */
public class MicrosoftAuthWorker extends SwingWorker<Object, Void> {

  private final Logger logger = LogManager.getLogger(this);
  private final String clientId;
  private final String deviceCode;
  private final int expiresIn;
  private final int interval;

  /**
   * Constructs a new MicrosoftAuthWorker with the specified parameters.
   *
   * @param clientId the client ID of the Azure application
   * @param deviceCode the device code
   * @param expiresIn the period of time (in seconds) the worker is allowed to run
   * @param interval the interval (in seconds) between each execution of the polling task
   */
  public MicrosoftAuthWorker(
      String clientId, String deviceCode, String expiresIn, String interval) {
    this.clientId = clientId;
    this.deviceCode = deviceCode;
    this.expiresIn = Integer.parseInt(expiresIn);
    this.interval = Integer.parseInt(interval);
  }

  /**
   * Schedules a new {@link io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthTask} at a fixed
   * rate, with the task being responsible for polling the device code. The polling process is
   * conducted in a separate thread, ensuring that it does not block the Swing Event Dispatch thread
   * (EDT).
   *
   * @return the result of the {@link io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthTask},
   *     or {@code null} if an error occurred
   */
  @Override
  protected Object doInBackground() {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    Future<?>[] future = new Future<?>[1];
    future[0] =
        service.scheduleAtFixedRate(
            new MicrosoftAuthTask(service, clientId, deviceCode),
            0,
            interval * 200L,
            TimeUnit.MILLISECONDS);

    try {
      return future[0].get(expiresIn * 1000L, TimeUnit.MILLISECONDS);
    } catch (ExecutionException ee) {
      this.logger.error("Error while scheduling authentication task", ee.getCause());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      this.logger.error("Interrupted while scheduling authentication task", ie);
    } catch (TimeoutException te) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));

      this.logger.error("Timed out while scheduling authentication task", te);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
