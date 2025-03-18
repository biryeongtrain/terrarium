package com.miir.atlas.world.gen.surface.providers;

import com.miir.atlas.AtlasConfig;
import dev.codedsakura.blossom.lib.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.miir.atlas.Util.getBlockState;
import static com.miir.atlas.world.gen.SurfaceBlockProvider.getRandom;


public class Badlands {
    //initialized vars
    public int x;
    public int y;
    //temp storage for band lifetime
    private static BlockState[] blockStates;
    private BlockState block;
    //config

    public static BadlandsConfig CONFIG;

    public Badlands(int x, int y){
        this.x = x;
        this.y = y;

        if(blockStates[0] == null){
            int lifetime = 0;
            for(int i = 0; i < blockStates.length; i++){
                if(lifetime <= 0){
                    lifetime = getBandLifetime();
                    block = getBlockFromList(getRandom(0, CONFIG.bands.length), CONFIG.bands);
                }
                blockStates[i] = block;
                lifetime--;
            }
        }

    }
    public BlockState getBlock(){
        //System.out.println(getBand(x, y).getBlock().getName().getString());
        return getBand(x, y);
    }
    public static void register(){
        CONFIG = ConfigManager.register(BadlandsConfig.class, "./atlas_surface/Badlands.json", newConfig -> CONFIG = newConfig);
        blockStates = new BlockState[CONFIG.maxY- CONFIG.minY];
    }
    private BlockState getBand(int x, int y){
        int index = (int) (y + ((x/ CONFIG.tilt) % blockStates.length));
        index = index > blockStates.length ? index - (blockStates.length + 1) : index;
        return blockStates[Math.min(Math.max(0, index), blockStates.length - 1)];
    }
    private BlockState getBlockFromList(int index, String[] args){
        return getBlockState(args[index]);
    }
    private int getBandLifetime(){
        return getRandom(CONFIG.bandWidthMin, CONFIG.bandWidthMax);
    }
}

