package ee.twentyten.util;

import ee.twentyten.thread.DaemonThreadFactory;
import ee.twentyten.thread.WorkerThreadFactory;

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

  public static Thread createDaemonThread(String name, Runnable r) {
    return DAEMON_THREAD_FACTORY.newThread(name, r);
  }

  public static Thread createWorkerThread(String name, Runnable r) {
    return WORKER_THREAD_FACTORY.newThread(name, r);
  }
}
