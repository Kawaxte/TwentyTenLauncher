package ee.twentyten.thread;

import ee.twentyten.custom.CustomThreadFactory;

public class DaemonThreadFactory implements CustomThreadFactory {

  @Override
  public synchronized Thread newThread(String name, Runnable r) {
    Thread t = new Thread(r);
    t.setName(name != null ? String.format("%s-%d", name, t.getId())
        : String.format("DaemonThread-%d", t.getId()));
    t.setDaemon(true);
    return t;
  }
}
