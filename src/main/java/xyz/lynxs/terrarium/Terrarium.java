package xyz.lynxs.terrarium;


import xyz.lynxs.terrarium.world.gen.TerrariumRegistries;
import xyz.lynxs.terrarium.world.gen.chunk.TerrariumChunkGenerator;

import dev.codedsakura.blossom.lib.config.ConfigManager;

import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static xyz.lynxs.terrarium.world.gen.HeightProvider.init;


public class Terrarium implements ModInitializer {
    public static final String MOD_ID = "terrarium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public  static TerrariumConfig CONFIG = ConfigManager.register(TerrariumConfig.class, "Terrarium.json", newConfig -> CONFIG = newConfig);
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Terrarium Loaded");
        //register surface rules
        TerrariumRegistries.register();
        // Register custom chunk generator
        Registry.register(Registries.CHUNK_GENERATOR, id(MOD_ID), TerrariumChunkGenerator.CODEC);

        init();
    }




}
