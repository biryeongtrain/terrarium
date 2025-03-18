package com.miir.atlas.world.gen;

import com.miir.atlas.world.gen.surface.providers.Badlands;
import net.minecraft.block.BlockState;

import java.util.Random;

import static com.miir.atlas.Atlas.seed;

public class SurfaceBlockProvider {
    private static Random random;

    /**
     * @param x
     * @param z
     * @param y
     * @return
     */

    public static BlockState getBlock(int x, int z, int y) {
        Badlands badlands = new Badlands(x, y);
        return badlands.getBlock();
    }


    public static int getRandom(int min, int max){
        return random.nextInt(min, max);
    }


    public static void init(){
        random = new Random(seed);
    }
    public static void register(){
        Badlands.register();
    }
}
