package net.minecraft.update;

abstract class MinecraftUpdater {

  public abstract void initLoader();

  public abstract void determinePackages();

  public abstract int retrievePackages(int[] sizes, int size);

  public abstract void downloadPackages();

  public abstract void extractDownloadedPackages();

  public abstract void updateClasspath();
}
