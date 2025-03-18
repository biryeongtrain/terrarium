package com.miir.atlas.world.gen.surface.providers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BadlandsConfig{
    double tilt = 10.0;
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
    int bandWidthMax = 20;
    int bandWidthMin = 2;
}
