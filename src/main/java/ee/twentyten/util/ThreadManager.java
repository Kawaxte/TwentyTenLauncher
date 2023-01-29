package ee.twentyten.util;

import ee.twentyten.core.thread.DaemonThreadFactory;
import ee.twentyten.core.thread.WorkerThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadManager {

  private static final ExecutorService workerThreadPool = Executors.newFixedThreadPool(10,
      new WorkerThreadFactory());
  private static final ExecutorService daemonThreadPool = Executors.newSingleThreadExecutor(
      new DaemonThreadFactory());

  private ThreadManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void executeWorkerTask(Runnable task) {
    workerThreadPool.execute(task);
  }

  public static void executeDaemonTask(Runnable task) {
    daemonThreadPool.execute(task);
  }
}
