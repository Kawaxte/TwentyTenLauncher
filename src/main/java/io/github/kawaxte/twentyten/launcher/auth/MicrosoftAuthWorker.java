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

public class MicrosoftAuthWorker extends SwingWorker<Object, Void> {

  private final Logger logger = LogManager.getLogger(this);
  private final String clientId;
  private final String deviceCode;
  private final int expiresIn;
  private final int interval;

  public MicrosoftAuthWorker(
      String clientId, String deviceCode, String expiresIn, String interval) {
    this.clientId = clientId;
    this.deviceCode = deviceCode;
    this.expiresIn = Integer.parseInt(expiresIn);
    this.interval = Integer.parseInt(interval);
  }

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
