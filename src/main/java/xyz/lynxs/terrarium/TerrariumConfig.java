package xyz.lynxs.terrarium;


import java.net.URI;

public class TerrariumConfig {
    public int zoom = 11;
    public int worldHeight = 512;
    public int startingY = 64;
    public int adjustXoffset = 100000;
    public int adjustZoffset = 200000;
    public URI ELEVATION_URL = URI.create("https://s3.amazonaws.com/elevation-tiles-prod/terrarium/");
    public URI TEMPERATURE_URL = URI.create("https://raw.githubusercontent.com/ly-nxs/terrarium-data/refs/heads/main/tiles/climate-monthly/");
    public String CACHE_DIR = "./tiles";
    public int month = 0;
    public double noise_biome_scale = 0.01;
}