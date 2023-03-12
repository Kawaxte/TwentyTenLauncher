package ee.twentyten.discord.callback;

import com.sun.jna.Callback;
import ee.twentyten.discord.DiscordUser;

public interface IReadyCallback extends Callback {

  void ready(DiscordUser request);
}
