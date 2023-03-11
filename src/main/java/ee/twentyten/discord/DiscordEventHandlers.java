package ee.twentyten.discord;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import ee.twentyten.discord.callback.IDisconnectedCallback;
import ee.twentyten.discord.callback.IErroredCallback;
import ee.twentyten.discord.callback.IReadyCallback;

@FieldOrder({"ready", "disconnected", "errored"})
public class DiscordEventHandlers extends Structure {

  public IReadyCallback ready;
  public IDisconnectedCallback disconnected;
  public IErroredCallback errored;

  public static class Builder {

    private final DiscordEventHandlers handlers;

    {
      this.handlers = new DiscordEventHandlers();
    }

    public Builder setReadyEventHandler(IReadyCallback drc) {
      this.handlers.ready = drc;
      return this;
    }

    public Builder setDisconnectedEventHandler(IDisconnectedCallback ddc) {
      this.handlers.disconnected = ddc;
      return this;
    }

    public Builder setErroredEventHandler(IErroredCallback dec) {
      this.handlers.errored = dec;
      return this;
    }

    public DiscordEventHandlers build() {
      return this.handlers;
    }
  }
}
