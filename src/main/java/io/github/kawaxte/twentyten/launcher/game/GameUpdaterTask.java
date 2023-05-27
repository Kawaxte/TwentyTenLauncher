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
import java.net.URL;

/**
 * This task performs the update process when run, downloading the necessary files and handling the
 * response. The response may indicate an error, a successful download without the Minecraft
 * profile, or a successful download with the Minecraft profile.
 *
 * <p>Upon receiving a response, the task downloads the necessary files and initiates the Minecraft
 * launch process with the appropriate parameters based on the response. If there are no files to be
 * downloaded, the task will skip the download process and proceed to the launch process.
 *
 * @see Runnable
 * @author Kawaxte
 * @since 1.5.1223_05
 */
public class GameUpdaterTask implements Runnable {

  private final URL[] urls;

  /**
   * Constructs a new update task with the given parameter.
   *
   * @param urls the URLs to download the files from
   */
  public GameUpdaterTask(URL[] urls) {
    this.urls = urls;
  }

  /**
   * Performs the update process, downloading the necessary files and handling the response.
   *
   * <p>When initialising, the state will be set to {@link EState#CHECK_CACHE} and the progress of
   * the task will be set to 5%. If the game is not cached, the task will download the necessary
   * files, move them to their respective locations, extract any archives and update the classpath.
   * If the game is cached, the task will skip the download process and proceed to the launch.
   *
   * <p>If no error occurs, the classpath is updated (just in case), the state will be set to {@link
   * EState#DONE} and the progress of the task will be set to 95%.
   *
   * @see GameUpdater#downloadPackages(URL[])
   * @see GameUpdater#extractDownloadedPackages()
   * @see GameUpdater#updateClasspath()
   */
  @Override
  public void run() {
    GameAppletWrapper.getInstance().setTaskState(EState.CHECK_CACHE.ordinal());
    GameAppletWrapper.getInstance().setTaskStateMessage(EState.CHECK_CACHE.getMessage());
    GameAppletWrapper.getInstance().setTaskProgressMessage(null);
    GameAppletWrapper.getInstance().setTaskProgress(5);
    if (!GameUpdater.isGameCached()) {
      GameUpdater.downloadPackages(urls);
      GameUpdater.extractDownloadedPackages();
    }

    if (!GameAppletWrapper.getInstance().isUpdaterTaskErrored()) {
      GameUpdater.updateClasspath();

      GameAppletWrapper.getInstance().setTaskState(EState.DONE.ordinal());
      GameAppletWrapper.getInstance().setTaskStateMessage(EState.DONE.getMessage());
      GameAppletWrapper.getInstance().setTaskProgressMessage(null);
      GameAppletWrapper.getInstance().setTaskProgress(95);
    }
  }
}
