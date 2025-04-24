package xyz.lynxs.terrarium;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.dimension.DimensionType;
import xyz.lynxs.terrarium.preset.presetConfig;
import xyz.lynxs.terrarium.world.gen.TerrariumRegistries;
import xyz.lynxs.terrarium.world.gen.biome.TerrariumBiomeSource;
import xyz.lynxs.terrarium.world.gen.chunk.TerrariumChunkGenerator;



import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static xyz.lynxs.terrarium.TerrariumConfig.load;
import static xyz.lynxs.terrarium.world.gen.HeightProvider.init;


public class Terrarium implements ModInitializer {
    public static final String MOD_ID = "terrarium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static presetConfig CONFIG = new presetConfig(13, 768, 64, 400000, 800000, 2, 0.01);
    public  static TerrariumConfig CONFIG1 = ConfigManager.register(TerrariumConfig.class, "Terrarium.json", newConfig -> CONFIG1 = newConfig);
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    // Server-side world load
    public static void onServerWorldLoad(MinecraftServer server, ServerWorld world) {
        try {
            RegistryKey<DimensionType> dimensionKey = world.getRegistryManager()
                    .get(RegistryKeys.DIMENSION_TYPE)
                    .getKey(world.getDimension())
                    .orElseThrow(() -> new IllegalStateException("Unknown dimension type"));

            if (dimensionKey.getValue().equals(id("terrarium"))) {
                CONFIG = load(CONFIG.getClass(), server.getSavePath(WorldSavePath.ROOT).resolve("terrarium.json").toString());
                init();
                TerrariumRegistries.register();
            }
        }
        catch (Exception e) {LOGGER.error(e.getMessage());}

    }


    @Override
    public void onInitialize() {
        LOGGER.info("Terrarium Loaded");
        // Register custom chunk generator
        Registry.register(
                Registries.BIOME_SOURCE,
                id("biome_source"),
                TerrariumBiomeSource.CODEC
        );

        // Register chunk generator
        Registry.register(
                Registries.CHUNK_GENERATOR,
                id("chunk_generator"),
                TerrariumChunkGenerator.CODEC
        );

        ServerWorldEvents.LOAD.register(Terrarium::onServerWorldLoad);

    }




}
