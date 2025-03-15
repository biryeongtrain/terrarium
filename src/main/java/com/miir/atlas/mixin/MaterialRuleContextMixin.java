package com.miir.atlas.mixin;

import com.miir.atlas.accessor.HeightProviderAccessor;
import com.miir.atlas.world.gen.HeightProvider;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MaterialRules.MaterialRuleContext.class)
public class MaterialRuleContextMixin implements HeightProviderAccessor {
    @Unique
    private RegistryEntry<HeightProvider> atlas_AMI;

    @Override
    public RegistryEntry<HeightProvider> atlas_getAMI() {
        return this.atlas_AMI;
    }

    @Override
    public void atlas_setAMI(RegistryEntry<HeightProvider> ami) {
        this.atlas_AMI = ami;
    }


}