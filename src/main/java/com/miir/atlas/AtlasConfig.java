package com.miir.atlas;


import dev.codedsakura.blossom.lib.teleport.TeleportConfig;
import org.jetbrains.annotations.Nullable;

public class AtlasConfig {
    public int additionalAlt = 1;
    TeleportConfig teleportation = new TeleportConfig(true);
    public int zoom = 11;
    public int worldHeight = 512;
    public double altitudeDropoff = 0.5;
    public int startingY = 64;
}