package me.jellysquid.mods.lithium.mixin.ai.poi;

import me.jellysquid.mods.lithium.common.world.interests.types.PointOfInterestTypeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

/**
 * Replaces the backing map type with a faster collection type which uses reference equality.
 */
@Mixin(PointOfInterestTypes.class)
public class PointOfInterestTypesMixin {
    @Shadow
    @Final
    protected static Set<BlockState> f_218067_;

    static {
        // POI_STATES_TO_TYPE = new Reference2ReferenceOpenHashMap<>(POI_STATES_TO_TYPE); TODO why it broke?

        PointOfInterestTypeHelper.init(f_218067_);
    }
}
