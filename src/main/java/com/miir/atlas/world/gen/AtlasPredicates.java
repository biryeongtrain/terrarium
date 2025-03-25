package com.miir.atlas.world.gen;

import com.miir.atlas.Atlas;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import static com.miir.atlas.world.gen.HeightProvider.getElevation;

public class AtlasPredicates {
    public static void register() {
        Registry.register(Registries.MATERIAL_CONDITION, Identifier.of("minecraft", "above_preliminary_surface"), AboveSurfaceMaterialCondition.CODEC.codec());
    }

    /**
     * reimplementation of the above_preliminary_surface rule that reads the atlas.
     * @param depth how far below the surface the rule should extend
     */
    record AboveSurfaceMaterialCondition(int depth) implements MaterialRules.MaterialCondition {
        static final CodecHolder<AtlasPredicates.AboveSurfaceMaterialCondition> CODEC = CodecHolder.of(
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                                Codec.INT.optionalFieldOf("depth", 5).forGetter(AboveSurfaceMaterialCondition::depth))
                        .apply(instance, AtlasPredicates.AboveSurfaceMaterialCondition::new)));

        @Override
        public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public MaterialRules.BooleanSupplier apply(final MaterialRules.MaterialRuleContext materialRuleContext) {
            class AboveSurfacePredicate
                    extends MaterialRules.FullLazyAbstractPredicate {
                AboveSurfacePredicate() {
                    super(materialRuleContext);
                }

                @Override
                protected boolean test() {
                    double elevation = getElevation(this.context.blockX, this.context.blockZ);
                    return this.context.blockY > elevation - AboveSurfaceMaterialCondition.this.depth;
                }
            }
            return new AboveSurfacePredicate();
        }
    }
}