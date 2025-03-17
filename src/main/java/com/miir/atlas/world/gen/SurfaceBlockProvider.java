package com.miir.atlas.world.gen;

import net.minecraft.block.BlockState;

import java.util.Random;

public class SurfaceBlockProvider {
    private static Random random;

    /**
     * @param x
     * @param z
     * @param y
     * @return
     */

    public static BlockState getBlock(int x, int z, int y) {

    }


    public static int getRandom(int min, int max){
        return random.nextInt(max);
    }


    public static void init(long seed){
        random = new Random(seed);
    }
}
