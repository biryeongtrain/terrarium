package com.miir.atlas.world.gen.surface.providers.badlands;

import dev.codedsakura.blossom.lib.config.ConfigManager;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.miir.atlas.Util.getBlockFromList;
import static com.miir.atlas.Util.getBlockState;
import static com.miir.atlas.world.gen.SurfaceBlockProvider.*;


public class Badlands {
    //initialized vars

    //temp storage for band lifetime
    private static BlockState[] bands;
    private static BlockState block;
    //config

    public static BadlandsConfig CONFIG;

    public Badlands(){
        CONFIG = ConfigManager.register(BadlandsConfig.class, "./atlas_surface/Badlands.json", newConfig -> CONFIG = newConfig);
        bands = new BlockState[CONFIG.maxY- CONFIG.minY];
        if(bands[0] == null){
            int lifetime = 0;
            for(int i = 0; i < bands.length; i++){
                if(lifetime <= 0){
                    lifetime = getBandLifetime();
                    block = getBlockFromList(getRandom(0, CONFIG.bands.length - 1), CONFIG.bands);
                }
                bands[i] = block;
                lifetime--;
            }
        }
    }
    public BlockState getBlock(int x, int y, int z){
        //System.out.println(getBand(x, y).getBlock().getName().getString());
        int steepness = getSteepness(x , z);

        return steepness < 2 ? getNoisePatches(x, z, CONFIG.scale, toBlockStateArr(CONFIG.surfaceFlat)) : getBand(x, y, z, CONFIG.scale, CONFIG.tilt, bands);
    }



    private static int getBandLifetime(){
        return getRandom(CONFIG.bandWidthMin, CONFIG.bandWidthMax);
    }


}

