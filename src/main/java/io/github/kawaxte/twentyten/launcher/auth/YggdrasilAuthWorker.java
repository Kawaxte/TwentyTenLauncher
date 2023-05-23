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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilAuthWorker extends SwingWorker<Object, Void> {

  private final String username;
  private final String password;
  private final String clientToken;
  private final Logger logger = LogManager.getLogger(this);

  public YggdrasilAuthWorker(String username, String password, String clientToken) {
    this.username = username;
    this.password = password;
    this.clientToken = clientToken;
  }

  @Override
  protected Object doInBackground() {
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<?> future = service.submit(new YggdrasilAuthTask(username, password, clientToken));
    try {
      return future.get();
    } catch (ExecutionException ee) {
      this.logger.error("Error while submitting authentication task", ee.getCause());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      this.logger.error("Interrupted while submitting authentication task", ie);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
