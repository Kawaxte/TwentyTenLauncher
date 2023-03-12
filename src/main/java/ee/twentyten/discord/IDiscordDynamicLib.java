package ee.twentyten.discord;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface IDiscordDynamicLib extends Library {

  IDiscordDynamicLib DISCORD_EXPORT = Native.load("discord-rpc", IDiscordDynamicLib.class);

  void Discord_Initialize(String applicationId, DiscordEventHandlers handlers,
      int autoRegister, String optionalSteamId);

  void Discord_Shutdown();

  void Discord_RunCallbacks();

  void Discord_UpdatePresence(DiscordRichPresence presence);

  void Discord_ClearPresence();
}
