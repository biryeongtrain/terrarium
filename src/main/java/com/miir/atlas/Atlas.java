package com.miir.atlas;


import com.miir.atlas.world.gen.AtlasPredicates;
import com.miir.atlas.world.gen.SurfaceBlockProvider;
import com.miir.atlas.world.gen.chunk.AtlasChunkGenerator;

import dev.codedsakura.blossom.lib.config.ConfigManager;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.miir.atlas.world.gen.SurfaceBlockProvider.init;


public class Atlas implements ModInitializer {
    public static final String MOD_ID = "atlas";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public  static AtlasConfig CONFIG = ConfigManager.register(AtlasConfig.class, "AtlasConfig.json", newConfig -> CONFIG = newConfig);
    public static long seed = 0;
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Atlas Loaded");

        String dirPath = "./config/BlossomMods/atlas_surface/";
        Path path = Paths.get(dirPath);

        // Check if the directory exists
        if (!Files.exists(path)) {
            try {
                // Create the directory (and parent directories if needed)
                Files.createDirectories(path);
                System.out.println("Directory created: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + e.getMessage());
            }
        }
        // Register custom chunk generator
        Registry.register(Registries.CHUNK_GENERATOR, id(MOD_ID), AtlasChunkGenerator.CODEC);

        SurfaceBlockProvider.register();
        AtlasPredicates.register();
        ServerWorldEvents.LOAD.register((server, world) -> {
            // Get the world seed
            seed = world.getSeed();
            init();
            // Print the seed to the console (or use it as needed)
            //System.out.println("World Seed: " + seed);
        });




    }




}
