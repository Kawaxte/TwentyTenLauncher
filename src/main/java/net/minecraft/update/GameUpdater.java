package net.minecraft.update;

import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;

@Getter
abstract class GameUpdater {

  public String stateMessage;
  public String taskMessage;
  public int percentage;
  public boolean isFatalErrorOccurred;
  boolean isNativesLoaded;
  URL[] urls;
  ClassLoader minecraftAppletLoader;

  int getTotalDownloadSize(int[] sizes, int size) {
    List<Callable<Integer>> retrieveTasks = new ArrayList<>();

    final HttpsURLConnection[] connection = {null};
    for (final URL fileUrl : this.urls) {
      retrieveTasks.add(new Callable<Integer>() {
        @Override
        public Integer call() {
          connection[0] = RequestUtils.performHttpsRequest(fileUrl, EMethod.HEAD,
              EHeader.NO_CACHE.getHeader());
          Objects.requireNonNull(connection[0], "connection == null!");
          try {
            return connection[0].getContentLength();
          } finally {
            connection[0].disconnect();
          }
        }
      });
    }

    ExecutorService retrieveService = Executors.newFixedThreadPool(this.urls.length);
    try {
      List<Future<Integer>> retrieveFutures = retrieveService.invokeAll(retrieveTasks);
      for (int i = 0; i < retrieveFutures.size(); i++) {
        Future<Integer> retrieveFuture = retrieveFutures.get(i);

        int fileSize = retrieveFuture.get();
        sizes[i] = fileSize;
        size += fileSize;
        this.percentage = 5 + (((i + 1) * 5) / this.urls.length);
      }
    } catch (ExecutionException ee) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "gui.exception.io.package.retrieveFailed"));
      LoggerUtils.logMessage("Failed to retrieve package sizes", ee, ELevel.ERROR);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      LoggerUtils.logMessage("Interrupted while retrieving package sizes", ie, ELevel.ERROR);
    } finally {
      retrieveService.shutdown();
    }
    return size;
  }

  void setFatalErrorMessage(String message) {
    this.isFatalErrorOccurred = true;

    this.stateMessage = MessageFormat.format(
        LanguageUtils.getString(LanguageUtils.getBundle(), "gui.string.fatalErrorMessage"),
        EState.getInstance().ordinal(), message);
    this.taskMessage = "";
  }

  boolean isContentLengthAvailable(final URL url, String urlString) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Integer> contentLengthFuture = executor.submit(new Callable<Integer>() {
      @Override
      public Integer call() {
        return FileUtils.getContentLength(url);
      }
    });

    try {
      int contentLength = contentLengthFuture.get();
      if (contentLength == -1) {
        Throwable fnfe = new FileNotFoundException(MessageFormat.format(
            LanguageUtils.getString(LanguageUtils.getBundle(), "gui.exception.fileNotFound"),
            urlString));

        this.setFatalErrorMessage(fnfe.getMessage());
        LoggerUtils.logMessage("Failed to check content length", fnfe, ELevel.ERROR);
        return true;
      }
      return false;
    } catch (ExecutionException ee) {
      this.setFatalErrorMessage(LanguageUtils.getString(LanguageUtils.getBundle(),
          "gui.exception.io.package.retrieveFailed"));
      LoggerUtils.logMessage("Failed to check content length", ee, ELevel.ERROR);
      return true;
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      LoggerUtils.logMessage("Interrupted while checking content length", ie, ELevel.ERROR);
      return true;
    } finally {
      executor.shutdown();
    }
  }

  abstract void determinePackage();

  abstract void downloadPackage();

  abstract void extractPackage();

  abstract void updateClasspath();
}
