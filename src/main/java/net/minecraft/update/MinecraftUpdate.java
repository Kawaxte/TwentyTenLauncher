package net.minecraft.update;

import java.io.IOException;

public abstract class MinecraftUpdate {

  abstract void determinePackage();

  abstract void downloadPackage() throws IOException;

  abstract void movePackage() throws IOException;
}
