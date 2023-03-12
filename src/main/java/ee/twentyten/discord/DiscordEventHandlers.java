package ee.twentyten.discord;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import ee.twentyten.discord.callback.IReadyCallback;

@FieldOrder({"ready"})
public class DiscordEventHandlers extends Structure {

  public IReadyCallback ready;

  public static class Builder {

    private final DiscordEventHandlers handlers;

    {
      this.handlers = new DiscordEventHandlers();
    }

    public Builder setReadyEventHandler(IReadyCallback drc) {
      this.handlers.ready = drc;
      return this;
    }

    public DiscordEventHandlers build() {
      return this.handlers;
    }
  }
}
