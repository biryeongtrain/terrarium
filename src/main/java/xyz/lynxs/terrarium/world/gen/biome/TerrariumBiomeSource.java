package xyz.lynxs.terrarium.world.gen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static xyz.lynxs.terrarium.world.gen.HeightProvider.getSectionSteepness;
import static xyz.lynxs.terrarium.world.gen.HeightProvider.pack;

public class TerrariumBiomeSource extends BiomeSource {
    private final List<BiomeEntry> biomeEntries;
    private final PerlinNoiseSampler noiseSampler;
    private static final int radius = 16;
    private static final int MAX_CACHE_SIZE = 1000;
    private static final Map<Long, int[][]> cache = new ConcurrentHashMap<>();

    public static final MapCodec<TerrariumBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeEntry.CODEC.listOf().fieldOf("biomes").forGetter(source -> source.biomeEntries)
            ).apply(instance, TerrariumBiomeSource::new)
    );

    public TerrariumBiomeSource(List<BiomeEntry> biomeEntries) {
        this(biomeEntries, Random.create());
    }

    public TerrariumBiomeSource(List<BiomeEntry> biomeEntries, Random random) {
        this.biomeEntries = biomeEntries;
        this.noiseSampler = new PerlinNoiseSampler(random);
    }

    public record BiomeEntry(
            RegistryEntry<Biome> biome,
            double steepness,
            double temperature,
            double noiseWeight
    ) {
        public static final Codec<BiomeEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(BiomeEntry::biome),
                        Codec.DOUBLE.fieldOf("steepness").forGetter(BiomeEntry::steepness),
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
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        double steepness = (x < radius || z < radius) ? 0 : getSteepness(x,z);
        double temperature = noise.sample(x, y, z).temperatureNoise();
        double noiseValue = noiseSampler.sample(x * 0.1, y * 0.1, z * 0.1);

        return findBestBiome(steepness, temperature, noiseValue);
    }

    private RegistryEntry<Biome> findBestBiome(double steepness, double temp, double noise) {
        BiomeEntry bestMatch = null;
        double bestScore = Double.POSITIVE_INFINITY;

        for (BiomeEntry entry : biomeEntries) {
            double score = Math.sqrt(
                    Math.pow(steepness - entry.steepness(), 2) * 2.0 +
                            Math.pow(temp - entry.temperature(), 2) +
                            Math.pow(noise * entry.noiseWeight(), 2)
            );

            if (score < bestScore) {
                bestScore = score;
                bestMatch = entry;
            }
        }

        return bestMatch != null ? bestMatch.biome() : getFallbackBiome();
    }

    private int getSteepness(int x, int z) {
        int xTile = x / radius;
        int zTile = z / radius;
        int xPixel = x % radius;
        int zPixel = z % radius;
        long key = pack(xTile, zTile);

        return cache.computeIfAbsent(key, k -> {
            if (cache.size() > MAX_CACHE_SIZE) {
                cache.clear(); // Simple eviction policy
            }
            int value = getSectionSteepness(xTile * radius, zTile * radius, radius);
            return fillAndReturn(value, radius);
        })[xPixel][zPixel];
    }

    private int[][] fillAndReturn(int value, int radius) {
        int[][] arr = new int[radius][radius];
        for (int[] row : arr) {
            Arrays.fill(row, value);
        }
        return arr;
    }

    private RegistryEntry<Biome> getFallbackBiome() {
        return biomeEntries.isEmpty() ? null : biomeEntries.get(0).biome();
    }
}