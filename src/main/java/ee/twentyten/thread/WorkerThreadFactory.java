package ee.twentyten.thread;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(String.format("WorkerThread-%d", t.getId()));
    t.setDaemon(false);
    return t;
  }
}
