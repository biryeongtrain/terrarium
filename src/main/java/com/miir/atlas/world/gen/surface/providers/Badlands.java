package com.miir.atlas.world.gen.surface.providers;

import com.miir.atlas.AtlasConfig;
import dev.codedsakura.blossom.lib.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.miir.atlas.Util.getBlockState;
import static com.miir.atlas.world.gen.SurfaceBlockProvider.*;


public class Badlands {
    //initialized vars
    public int x;
    public int y;
    public int z;
    //temp storage for band lifetime
    private static BlockState[] blockStates;
    private BlockState block;
    //config

    public static BadlandsConfig CONFIG;

    public Badlands(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        if(blockStates[0] == null){
            int lifetime = 0;
            for(int i = 0; i < blockStates.length; i++){
                if(lifetime <= 0){
                    lifetime = getBandLifetime();
                    block = getBlockFromList(getRandom(0, CONFIG.bands.length - 1), CONFIG.bands);
                }
                blockStates[i] = block;
                lifetime--;
            }
        }

    }
    public BlockState getBlock(){
        //System.out.println(getBand(x, y).getBlock().getName().getString());
        return getBand(x, y, z);
    }
    public static void register(){
        CONFIG = ConfigManager.register(BadlandsConfig.class, "./atlas_surface/Badlands.json", newConfig -> CONFIG = newConfig);
        blockStates = new BlockState[CONFIG.maxY- CONFIG.minY];
    }

    private BlockState getBand(int x, int y, int z) {
        // Define band height and transition zone
       // Transition zone is 5 blocks tall

        // Get the noise value for this (x, z) position
        double noise = getNoise(x, z, CONFIG.scale);

        // Adjust the y-coordinate based on the noise
        double adjustedY = y + (CONFIG.tilt * noise);

        // Calculate the band index with roll-over
        int bandIndex = (int) adjustedY % blockStates.length;

        // Ensure the band index is non-negative
        bandIndex = (bandIndex + blockStates.length) % blockStates.length;

        // Return the corresponding BlockState
        return blockStates[bandIndex];
    }

    private BlockState getBlockFromList(int index, String[] args){
        return getBlockState(args[index]);
    }

    private int getBandLifetime(){
        return getRandom(CONFIG.bandWidthMin, CONFIG.bandWidthMax);
    }
}

