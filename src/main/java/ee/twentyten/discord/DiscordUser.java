package ee.twentyten.discord;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"userId", "username", "discriminator"})
public class DiscordUser extends Structure {

  public String userId;
  public String username;
  public String discriminator;
}
