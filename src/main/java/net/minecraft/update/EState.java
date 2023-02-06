package net.minecraft.update;

import lombok.Getter;

public enum EState {
  INIT_STATE("Initialising loader"),
  CACHE_STATE("Checking cache for existing files"),
  DETERMINE_STATE("Determining packages"),
  RETRIEVE_STATE("Retrieving packages"),
  DOWNLOAD_STATE("Downloading packages"),
  MOVE_STATE("Moving packages"),
  CLASSPATH_STATE("Updating classpath"),
  DONE_STATE("Done loading");

  private static final EState[] values = values();

  @Getter
  private static EState state;
  @Getter
  private final String message;

  EState(String message) {
    this.message = message;
  }

  public static void setState(EState state) {
    EState.state = state;
  }
}
