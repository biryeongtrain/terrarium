package com.miir.atlas.world.gen;

import com.miir.atlas.world.gen.surface.providers.Badlands;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.ThreadSafeRandom;


import static com.miir.atlas.Atlas.seed;
import static com.miir.atlas.world.gen.HeightProvider.getElevation;

public class SurfaceBlockProvider {
    public static Random random;
    private static PerlinNoiseSampler noise;

    //Biome defs
    /**
     * @param x
     * @param z
     * @param y
     * @return
     */

    public static BlockState getBlock(int x, int z, int y) {
        Badlands badlands = new Badlands(x, y, z);
        return badlands.getBlock();
    }


    public static int getRandom(int min, int max){
        return random.nextBetween(min, max);
    }


    public static void init(){
        random = new LocalRandom(seed);
        noise = new PerlinNoiseSampler(random);
    }
    public static void register(){
        Badlands.register();
    }

    public static double getNoise(double x, double z, double scale) {
        // Scale the coordinates to control the noise frequency
        // Adjust this to control the smoothness of the noise
        return noise.sample(x * scale, 0, z * scale);
    }
    public static double getSteepness(int x, int z) {
        // Get the height of the center block
        int centerHeight = getElevation(x, z);
        return 0;
        // Check the 4 cardinal directions (north, south, east, west)
    }
}
