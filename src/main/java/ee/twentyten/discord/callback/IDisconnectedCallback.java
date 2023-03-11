package ee.twentyten.discord.callback;

import com.sun.jna.Callback;

public interface IDisconnectedCallback extends Callback {

  void apply(int errorCode, String message);

}
