package com.miir.atlas;


import com.miir.atlas.world.gen.chunk.AtlasChunkGenerator;

import dev.codedsakura.blossom.lib.config.ConfigManager;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.miir.atlas.world.gen.SurfaceBlockProvider.init;


public class Atlas implements ModInitializer {
    public static final String MOD_ID = "atlas";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public  static AtlasConfig CONFIG = ConfigManager.register(AtlasConfig.class, "AtlasConfig.json", newConfig -> CONFIG = newConfig);
    public static long seed = 0;
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            // Get the world seed
            seed = world.getSeed();
            init(seed);
            // Print the seed to the console (or use it as needed)
            //System.out.println("World Seed: " + seed);
        });
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Atlas Loaded");
        // Register custom chunk generator
        Registry.register(Registries.CHUNK_GENERATOR, id(MOD_ID), AtlasChunkGenerator.CODEC);

        register();




    }




}
