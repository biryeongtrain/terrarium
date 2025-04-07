package xyz.lynxs.terrarium;



public class TerrariumConfig {
    public int zoom = 11;
    public int worldHeight = 512;
    public int startingY = 64;
    public int adjustXoffset = 100000;
    public int adjustZoffset = 200000;
    public String ELEVATION_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";
    public String TEMPERATURE_URL = "https://raw.githubusercontent.com/ly-nxs/terrarium-data/refs/heads/main/tiles/climate-monthly/";
    public String CACHE_DIR = "./tiles";
    public int month = 0;
}