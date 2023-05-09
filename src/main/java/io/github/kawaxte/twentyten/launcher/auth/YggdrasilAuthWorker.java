package io.github.kawaxte.twentyten.launcher.auth;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import javax.swing.SwingWorker;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilAuthWorker extends SwingWorker<Object, Void> {

  private final String username;
  private final String password;
  private final String clientToken;
  private final Logger logger;

  {
    this.logger = LogManager.getLogger(this);
  }

  public YggdrasilAuthWorker(String username, String password, String clientToken) {
    this.username = username;
    this.password = password;
    this.clientToken = clientToken;
  }

  @Override
  protected Object doInBackground() {
    val service = Executors.newSingleThreadExecutor();
    val future = service.submit(new YggdrasilAuthTask(username, password, clientToken));
    try {
      return future.get();
    } catch (ExecutionException ee) {
      this.logger.error("Error while submitting authentication task", ee.getCause());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      this.logger.error("Interrupted while submitting authentication task", ie);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
