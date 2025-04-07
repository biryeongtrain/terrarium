package xyz.lynxs.terrarium.world.gen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static xyz.lynxs.terrarium.Terrarium.CONFIG;
import static xyz.lynxs.terrarium.world.gen.BiomeProvider.getTemperature;
import static xyz.lynxs.terrarium.world.gen.HeightProvider.*;

public class TerrariumBiomeSource extends BiomeSource {
    private final List<BiomeEntry> biomeEntries;
    private final PerlinNoiseSampler noiseSampler;
    private final RegistryEntry<ChunkGeneratorSettings> settings;


    public static final MapCodec<TerrariumBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeEntry.CODEC.listOf().fieldOf("biomes").forGetter(source -> source.biomeEntries),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(source -> source.settings)
            ).apply(instance, TerrariumBiomeSource::new)
    );

    public TerrariumBiomeSource(List<BiomeEntry> biomeEntries, RegistryEntry<ChunkGeneratorSettings> settings ) {
        this(biomeEntries, Random.create(), settings);
    }

    public TerrariumBiomeSource(List<BiomeEntry> biomeEntries, Random random, RegistryEntry<ChunkGeneratorSettings> settings) {
        this.biomeEntries = biomeEntries;
        this.noiseSampler = new PerlinNoiseSampler(random);
        this.settings = settings;
    }


    public record BiomeEntry(
            RegistryEntry<Biome> biome,
            double elevation,
            double temperature,
            double noiseWeight
    ) {
        public static final Codec<BiomeEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(BiomeEntry::biome),
                        Codec.DOUBLE.fieldOf("elevation").forGetter(BiomeEntry::elevation),
                        Codec.DOUBLE.fieldOf("temperature").forGetter(BiomeEntry::temperature),
                        Codec.DOUBLE.fieldOf("noise_weight").forGetter(BiomeEntry::noiseWeight)
                ).apply(instance, BiomeEntry::new)
        );
    }

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return biomeEntries.stream().map(BiomeEntry::biome);
    }


    @Override
    public RegistryEntry<Biome> getBiome(int x, int elevation, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int adjustedX = x + CONFIG.adjustXoffset;
        int adjustedZ = z + CONFIG.adjustZoffset;
        double temperature = getTemperature(adjustedX, adjustedZ, CONFIG.zoom);
        temperature =  temperature > 1 ? noise.sample(x, elevation, z).temperatureNoise() : temperature;
        double height = getLocalElevation((adjustedX < 0 || adjustedZ < 0 || adjustedX > size || adjustedZ > size) ? 0 : getElevation(adjustedX , adjustedZ));
        double noiseValue = getNoiseValue(adjustedX, elevation, adjustedZ);

        return findBestBiome(height, temperature, noiseValue);
    }

    private double getNoiseValue(int x, int elevation, int z) {
        return MathHelper.clamp(noiseSampler.sample(x * 0.1, elevation * 0.1, z * 0.1), -1.0, 1.0);
    }

    private RegistryEntry<Biome> findBestBiome(double height, double temperature, double noise) {
        return biomeEntries.stream()
                .min(Comparator.comparingDouble(b ->
                        Math.pow(b.elevation() - height, 2) * 0.7 +
                                Math.pow(b.temperature() - temperature, 2) +
                                Math.pow(b.noiseWeight() - noise, 2) * 0.5
                ))
                .orElseThrow().biome();
    }



    double getLocalElevation(int y){
        return MathHelper.clamp((y - settings.value().seaLevel()) / (double)(settings.value().generationShapeConfig().height() - settings.value().seaLevel()), -1.0, 1.0);
    }
    double truncate(double num, int places){
        return  (int)(num * Math.pow(10, places)) / Math.pow(10, places); // truncatedNumber will be 10.78
    }
    @Override
    public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        int i = BiomeCoords.fromBlock(pos.getX());
        int j = BiomeCoords.fromBlock(pos.getY());
        int k = BiomeCoords.fromBlock(pos.getZ());
        int adjustedZ = k + CONFIG.adjustZoffset;
        int adjustedX = i + CONFIG.adjustXoffset;

        info.add(
                "Biome builder PV: "
                        + " Elevation: "
                        + truncate(getLocalElevation(j), 3)
                        + " Temperature: "
                        + truncate(getTemperature(adjustedX, adjustedZ, CONFIG.zoom), 3)
                        + " Noise: "
                        + truncate(getNoiseValue(i, j, k), 3)
        );
    }

}