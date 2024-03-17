package me.jellysquid.mods.lithium.mixin.experimental.entity.block_caching.fluid_pushing;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.jellysquid.mods.lithium.common.entity.block_tracking.BlockCache;
import me.jellysquid.mods.lithium.common.entity.block_tracking.BlockCacheProvider;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidType;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements BlockCacheProvider {
    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract World getWorld();

    @Shadow
    protected Object2DoubleMap<FluidType> forgeFluidTypeHeight;

    @Inject(
            method = "updateFluidHeightAndDoFluidPushing",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipFluidSearchUsingCache(CallbackInfo ci) {
        BlockCache bc = this.getUpdatedBlockCache((Entity)(Object)this);
        var fluidMap = bc.getCachedFluidHeightMap();
        if (fluidMap != null) {
            // Avoid calling putAll when we know the map is empty
            if(!fluidMap.isEmpty()) {
                //Note: If the region is unloaded in target method, this still puts 0. However, default return value is 0, and vanilla doesn't use any method that reveals this difference.
                this.forgeFluidTypeHeight.putAll(fluidMap);
            }
            // Skip expensive logic
            ci.cancel();
        }
    }

    @Inject(
            method = "lambda$updateFluidHeightAndDoFluidPushing$26",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;length()D", ordinal = 0, shift = At.Shift.BEFORE)
    )
    private void cacheFluidSearchResult(FluidType type, MutableTriple<Double, Vec3d, Integer>  interim, CallbackInfo ci) {
        BlockCache bc = this.getBlockCache();
        if (bc.isTracking() && interim.getMiddle().lengthSquared() == 0d) {
            double fluidHeight = interim.getLeft();
            /* TODO Radium: we do not have access to that local inside the lambda
            if (touchingFluid == (fluidHeight == 0d)) {
                throw new IllegalArgumentException("Expected fluid touching IFF fluid height is not 0! Fluid height: " + fluidHeight + " Touching fluid: " + touchingFluid + " Fluid Tag: " + fluid);
            }
             */
            bc.setCachedFluidHeight(type, fluidHeight);
        }
    }
}
