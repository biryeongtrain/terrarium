package com.miir.atlas.world.gen;

import com.miir.atlas.world.gen.surface.providers.badlands.Badlands;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;


import static com.miir.atlas.Atlas.seed;
import static com.miir.atlas.Util.getBlockState;
import static com.miir.atlas.world.gen.HeightProvider.getElevation;

public class SurfaceBlockProvider {
    public static Random random;
    private static PerlinNoiseSampler noise;
    public Badlands badlands;
    public long seed;

    public SurfaceBlockProvider(long seed){

        this.seed = seed;
        random = new LocalRandom(this.seed);
        noise = new PerlinNoiseSampler(random);
        this.badlands = new Badlands();
    }

    public BlockState getBlock(int x, int z, int y, int elevation) {
        //if(elevation - y < CONFIG.stoneDepth) {
            return badlands.getBlock(x, y, z);
        //}
    }


    public static int getRandom(int min, int max){
        return random.nextBetween(min, max);
    }



    public static double getNoise(double x, double z, double scale) {
        // Scale the coordinates to control the noise frequency
        // Adjust this to control the smoothness of the noise
        return noise.sample(x * scale, 0, z * scale);
    }
    public static int getSteepness(int x, int z) {
        // Get the height of the center block
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for(int i = x > 0 ? -1 : 0; i < 2; i++){
            for(int j = z > 0 ? -1 : 0; j < 2; j++){
                int elevation = getElevation(x + i, z + j);
                min = Math.min(elevation, min);
                max = Math.max(elevation, max);
            }
        }
        // 64/72 < 1; 64/64 = 1; -32 -- 34 = 2 yay it works
        return max - min;
    }
    public static BlockState getNoisePatches(int x, int z, double scale, BlockState[] blockStates){
        double noise = getNoise(x, z, scale);
        double increment = (double) 2 /blockStates.length;
        return blockStates[(int) Math.abs(noise/increment) * (blockStates.length - 1)];
    }
    public static BlockState getBand(int x, int y, int z, double scale, double tilt, BlockState[] blockStates) {
        // Define band height and transition zone
        // Transition zone is 5 blocks tall

        // Get the noise value for this (x, z) position
        double noise = getNoise(x, z, scale);

        // Adjust the y-coordinate based on the noise
        double adjustedY = y + (tilt * noise);

        // Calculate the band index with roll-over
        int bandIndex = (int) adjustedY % blockStates.length;

        // Ensure the band index is non-negative
        bandIndex = (bandIndex + blockStates.length) % blockStates.length;

        // Return the corresponding BlockState
        return blockStates[bandIndex];
    }
    public static BlockState[] toBlockStateArr(String[] args){
        BlockState[] arr = new BlockState[args.length];
        for(int i = 0; i < args.length; i++){
            arr[i] = getBlockState(args[i]);
        }
        return arr;
    }
}
