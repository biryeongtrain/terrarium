package com.miir.atlas;


import com.miir.atlas.world.gen.AtlasPredicates;
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



public class Atlas implements ModInitializer {
    public static final String MOD_ID = "atlas";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public  static AtlasConfig CONFIG = ConfigManager.register(AtlasConfig.class, "AtlasConfig.json", newConfig -> CONFIG = newConfig);
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Atlas Loaded");
        //register surface rules
        AtlasPredicates.register();
        // Register custom chunk generator
        Registry.register(Registries.CHUNK_GENERATOR, id(MOD_ID), AtlasChunkGenerator.CODEC);


    }




}
