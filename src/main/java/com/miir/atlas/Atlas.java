package com.miir.atlas;


import com.miir.atlas.world.gen.HeightProvider;
import com.miir.atlas.world.gen.chunk.AtlasChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Atlas implements ModInitializer {
    public static final String MOD_ID = "atlas";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Atlas Loaded");
        // Register custom chunk generator
        Registry.register(Registries.CHUNK_GENERATOR, id(MOD_ID), AtlasChunkGenerator.CODEC);


    }


}
