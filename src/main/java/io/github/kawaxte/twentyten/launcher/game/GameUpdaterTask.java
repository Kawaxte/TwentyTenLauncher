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
    if (!GameUpdater.isGameCached()) {
      GameUpdater.downloadPackages(urls);
      GameUpdater.extractDownloadedPackages();
    }

    if (!GameAppletWrapper.instance.isUpdaterTaskErrored()) {
      GameUpdater.updateClasspath();

      GameAppletWrapper.instance.setTaskState(EState.DONE.ordinal());
      GameAppletWrapper.instance.setTaskStateMessage(EState.DONE.getMessage());
      GameAppletWrapper.instance.setTaskProgressMessage("");
      GameAppletWrapper.instance.setTaskProgress(95);
    }
  }
}
