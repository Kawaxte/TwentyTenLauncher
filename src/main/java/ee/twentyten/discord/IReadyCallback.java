package ee.twentyten.discord;

import com.sun.jna.Callback;

public interface IReadyCallback extends Callback {

  void onReady(DiscordUser request);
}
