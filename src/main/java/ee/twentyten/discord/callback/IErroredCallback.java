package ee.twentyten.discord.callback;

import com.sun.jna.Callback;

public interface IErroredCallback extends Callback {

  void apply(int errorCode, String message);
}
