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
package io.github.kawaxte.twentyten.launcher.game;

import io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper;
import java.applet.Applet;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import javax.swing.SwingWorker;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameUpdaterWorker extends SwingWorker<Applet, Void> {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(GameUpdaterWorker.class);
  }

  private final URL[] urls;

  public GameUpdaterWorker(URL[] urls) {
    this.urls = urls;
  }

  @Override
  protected Applet doInBackground() {
    val service = Executors.newSingleThreadExecutor();
    val future = service.submit(new GameUpdaterTask(urls));
    try {
      future.get();

      if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
        return (Applet)
            GameAppletWrapper.instance
                .getMcAppletClassLoader()
                .loadClass("net.minecraft.client.MinecraftApplet")
                .newInstance();
      }
    } catch (ExecutionException ee) {
      LOGGER.error("Error while submitting updater task", ee.getCause());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while submitting updater task", ie);
    } catch (ClassNotFoundException cnfe) {
      LOGGER.error("Cannot find MinecraftApplet", cnfe);
    } catch (InstantiationException ie) {
      LOGGER.error("Cannot instantiate MinecraftApplet", ie);
    } catch (IllegalAccessException iae) {
      LOGGER.error("Cannot access MinecraftApplet", iae);
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  protected void done() {
    try {
      val applet = this.get();
      if (Objects.nonNull(applet)) {
        GameAppletWrapper.instance.replace(applet);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while replacing applet", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while replacing applet", ee.getCause());
    }
  }
}
