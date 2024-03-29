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

package ch.kawaxte.launcher.minecraft;

import ch.kawaxte.launcher.ui.MinecraftAppletWrapper;
import com.google.api.client.http.GenericUrl;
import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class scheduling a {@link MinecraftUpdateTask}.
 *
 * @see javax.swing.SwingWorker
 * @since 1.5.1223_05
 */
public class MinecraftUpdateWorker extends SwingWorker<Applet, Void> {

  private static final Logger LOGGER;

  static {
    LOGGER = LoggerFactory.getLogger(MinecraftUpdateWorker.class);
  }

  private final GenericUrl[] urls;

  /**
   * Constructs a new GameUpdaterWorker with the specified URLs.
   *
   * @param urls the URLs to download the files from
   */
  public MinecraftUpdateWorker(GenericUrl[] urls) {
    this.urls = urls;
  }

  @Override
  protected Applet doInBackground() {
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<?> future = service.submit(new MinecraftUpdateTask(urls));
    try {
      future.get();

      if (!MinecraftAppletWrapper.getInstance().isUpdaterTaskErrored()) {
        return (Applet)
            MinecraftAppletWrapper.getInstance()
                .getMcAppletClassLoader()
                .loadClass("net.minecraft.client.MinecraftApplet")
                .getDeclaredConstructor()
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
    } catch (InvocationTargetException ite) {
      LOGGER.error("Cannot invoke MinecraftApplet", ite);
    } catch (NoSuchMethodException nsme) {
      LOGGER.error("Cannot find MinecraftApplet constructor", nsme);
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  protected void done() {
    try {
      Applet applet = this.get();
      if (Objects.nonNull(applet)) {
        MinecraftAppletWrapper.getInstance().replace(applet);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while replacing applet", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while replacing applet", ee.getCause());
    }
  }
}
