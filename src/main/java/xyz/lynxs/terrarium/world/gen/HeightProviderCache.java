package xyz.lynxs.terrarium.world.gen;

import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.util.math.ChunkPos;

import static xyz.lynxs.terrarium.world.gen.HeightProvider.getElevation;

public class HeightProviderCache {
    private static final ThreadLocal<Long2IntMap> threadCache =
            ThreadLocal.withInitial(Long2IntOpenHashMap::new);

    public static int getHeight(int x, int z) {
        Long2IntMap cache = threadCache.get();
        long key = ChunkPos.toLong(x, z);
        return cache.computeIfAbsent(key, k -> getElevation(x, z));
    }

    public static void clearCache() {
        threadCache.get().clear();
    }
}