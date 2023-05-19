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

public class GameUpdaterTask implements Runnable {

  private final URL[] urls;

  public GameUpdaterTask(URL[] urls) {
    this.urls = urls;
  }

  @Override
  public void run() {
    GameAppletWrapper.instance.setTaskState(EState.CHECK_CACHE.ordinal());
    GameAppletWrapper.instance.setTaskStateMessage(EState.CHECK_CACHE.getMessage());
    GameAppletWrapper.instance.setTaskProgressMessage(null);
    GameAppletWrapper.instance.setTaskProgress(5);
    if (!GameUpdater.isGameCached()) {
      GameUpdater.downloadPackages(urls);
      GameUpdater.extractDownloadedPackages();
    }

    if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
      GameUpdater.updateClasspath();

      GameAppletWrapper.instance.setTaskState(EState.DONE.ordinal());
      GameAppletWrapper.instance.setTaskStateMessage(EState.DONE.getMessage());
      GameAppletWrapper.instance.setTaskProgressMessage(null);
      GameAppletWrapper.instance.setTaskProgress(95);
    }
  }
}
