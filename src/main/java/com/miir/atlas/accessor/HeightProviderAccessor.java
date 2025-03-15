package com.miir.atlas.accessor;

import com.miir.atlas.world.gen.HeightProvider;
import net.minecraft.registry.entry.RegistryEntry;

public interface HeightProviderAccessor {
    RegistryEntry<HeightProvider> atlas_getAMI();
    void atlas_setAMI(RegistryEntry<HeightProvider> ami);
}