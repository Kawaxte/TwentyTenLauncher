package ee.twentyten.core.thread;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(String.format("DaemonThread-%d", t.getId()));
    t.setDaemon(true);
    return t;
  }
}
