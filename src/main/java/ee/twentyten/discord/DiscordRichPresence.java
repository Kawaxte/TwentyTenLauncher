package ee.twentyten.discord;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import java.util.Objects;

@FieldOrder({"state", "details", "startTimestamp", "endTimestamp", "largeImageKey",
    "largeImageText",
    "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "matchSecret",
    "spectateSecret", "joinSecret", "instance"})
public class DiscordRichPresence extends Structure {

  public String state;
  public String details;
  public long startTimestamp;
  public long endTimestamp;
  public String largeImageKey;
  public String largeImageText;
  public String smallImageKey;
  public String smallImageText;
  public String partyId;
  public int partySize;
  public int partyMax;
  @Deprecated
  public String matchSecret;
  public String spectateSecret;
  public String joinSecret;
  @Deprecated
  public int instance;

  public static class Builder {

    private final DiscordRichPresence presence;

    public Builder() {
      this.presence = new DiscordRichPresence();
    }

    public Builder(DiscordRichPresence presence) {
      this.presence = presence;
      this.presence.state = presence.state;
      this.presence.details = presence.details;
      this.presence.startTimestamp = presence.startTimestamp;
      this.presence.endTimestamp = presence.endTimestamp;
      this.presence.largeImageKey = presence.largeImageKey;
      this.presence.largeImageText = presence.largeImageText;
      this.presence.smallImageKey = presence.smallImageKey;
      this.presence.smallImageText = presence.smallImageText;
      this.presence.partyId = presence.partyId;
      this.presence.partySize = presence.partySize;
      this.presence.partyMax = presence.partyMax;
      this.presence.spectateSecret = presence.spectateSecret;
      this.presence.joinSecret = presence.joinSecret;
      this.presence.instance = presence.instance;
    }

    public Builder setState(String state) {
      Objects.requireNonNull(state, "state == null!");
      this.presence.state = state;
      return this;
    }

    public Builder setDetails(String details) {
      Objects.requireNonNull(details, "details == null!");
      this.presence.details = details;
      return this;
    }

    public Builder setStartTimestamp(long start) {
      this.presence.startTimestamp = start;
      return this;
    }

    public Builder setEndTimestamp(long end) {
      this.presence.endTimestamp = end;
      return this;
    }

    public Builder setLargeImage(String key, String text) {
      Objects.requireNonNull(key, "key == null!");
      this.presence.largeImageKey = key;

      Objects.requireNonNull(text, "text == null!");
      this.presence.largeImageText = text;
      return this;
    }

    public Builder setSmallImage(String key, String text) {
      Objects.requireNonNull(key, "key == null!");
      this.presence.smallImageKey = key;

      Objects.requireNonNull(text, "text == null!");
      this.presence.smallImageText = text;
      return this;
    }

    public Builder setParty(String id, int size, int max) {
      Objects.requireNonNull(id, "id == null!");
      this.presence.partyId = id;

      this.presence.partySize = size;
      this.presence.partyMax = max;
      return this;
    }

    @Deprecated
    public Builder setSecrets(String match, String join, String spectate) {
      Objects.requireNonNull(match, "match == null!");
      this.presence.matchSecret = match;

      Objects.requireNonNull(join, "join == null!");
      this.presence.joinSecret = join;

      Objects.requireNonNull(spectate, "spectate == null!");
      this.presence.spectateSecret = spectate;
      return this;
    }

    public Builder setSecrets(String join, String spectate) {
      Objects.requireNonNull(join, "join == null!");
      this.presence.joinSecret = join;

      Objects.requireNonNull(spectate, "spectate == null!");
      this.presence.spectateSecret = spectate;
      return this;
    }

    @Deprecated
    public Builder setInstance(boolean instance) {
      this.presence.instance = instance ? 1 : 0;
      return this;
    }

    public DiscordRichPresence build() {
      return this.presence;
    }
  }
}
