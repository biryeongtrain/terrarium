package com.miir.atlas.world.gen.surface.providers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;



public class Badlands {
    //initialized vars
    public int x;
    public int y;

    //config


    public Badlands(int x, int y){
        this.x = x;
        this.y = y;
    }
    public BlockState
}

class BadlandsConfig{
    double tilt = 10.0;
    int minY = 64;
    int maxY = 256;
    float latitudeMin = 0;
    float latitudeMax = 45;
    BlockState[] bands = new BlockState[]{
          Blocks.TERRACOTTA.getDefaultState(),

    };
}