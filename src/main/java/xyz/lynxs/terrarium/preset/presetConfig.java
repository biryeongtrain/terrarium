package xyz.lynxs.terrarium.preset;

public class presetConfig {
    public int zoom;
    public int worldHeight;
    public int startingY;
    public int adjustXoffset;
    public int adjustZoffset;
    public int month;
    public double noise_biome_scale;

    public presetConfig(int zoom, int worldHeight, int startingY, int adjustXoffset, int adjustZoffset, int month, double noise_biome_scale){
        this.adjustXoffset = adjustXoffset;
        this.zoom = zoom;
        this.worldHeight = worldHeight;
        this.startingY = startingY;
        this.adjustZoffset = adjustZoffset;
        this.month = month;
        this.noise_biome_scale = noise_biome_scale;
    }
}
