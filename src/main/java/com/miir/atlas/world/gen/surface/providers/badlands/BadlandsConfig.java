package com.miir.atlas.world.gen.surface.providers.badlands;

public class BadlandsConfig{
    double tilt = 10.0;
    double scale = 0.1;
    int minY = 64;
    int maxY = 256;
    float latitudeMin = 0;
    float latitudeMax = 45;
    String default_block = "RED_SAND";
    String[] bands = new String[]{
          "TERRACOTTA",
            "RED_TERRACOTTA",
            "ORANGE_TERRACOTTA",
            "YELLOW_TERRACOTTA",
            "CYAN_TERRACOTTA",
            "GRAY_TERRACOTTA",
            "BLACK_TERRACOTTA",
            "WHITE_TERRACOTTA"
    };
    String[] surfaceFlat = new String[]{
           "SAND",
           "RED_SAND",
           "COARSE_DIRT"
    };
    int bandWidthMax = 20;
    int bandWidthMin = 2;
}
