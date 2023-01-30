package ee.twentyten.util;

import ee.twentyten.thread.DaemonThreadFactory;
import ee.twentyten.thread.WorkerThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadManager {

  private static final DaemonThreadFactory DAEMON_THREAD_FACTORY;
  private static final WorkerThreadFactory WORKER_THREAD_FACTORY;

  static {
    DAEMON_THREAD_FACTORY = new DaemonThreadFactory();
    WORKER_THREAD_FACTORY = new WorkerThreadFactory();
  }

  private ThreadManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Thread createDaemonThread(Runnable r) {
    return DAEMON_THREAD_FACTORY.newThread(r);
  }

  public static Thread createWorkerThread(Runnable r) {
    return WORKER_THREAD_FACTORY.newThread(r);
  }

  public static void startThread(Thread t) {
    t.start();
  }

  public static void runInThreadPool(Runnable r) {
    ExecutorService es = Executors.newSingleThreadExecutor(WORKER_THREAD_FACTORY);
    es.submit(r);
    es.shutdown();
  }
}
