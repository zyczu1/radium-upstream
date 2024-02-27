package me.jellysquid.mods.lithium.common;

import me.jellysquid.mods.lithium.common.ai.pathing.BlockStatePathingCache;
import me.jellysquid.mods.lithium.common.config.LithiumConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.callback.BakeCallback;

@Mod(LithiumMod.MODID)
public class LithiumMod {
    public static final String MODID = "radium";
    public static LithiumConfig CONFIG;

    public LithiumMod(IEventBus modEventBus) {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }

        modEventBus.addListener(EventPriority.LOWEST, this::modifyRegistries);

    }

    private void modifyRegistries(ModifyRegistriesEvent event) {
        if(BlockStatePathingCache.class.isAssignableFrom(BlockState.class)) {
            Registries.BLOCK.addCallback((BakeCallback<Block>) registry -> {
                for(Block block : registry) {
                    for(BlockState state : block.getStateManager().getStates()) {
                        ((BlockStatePathingCache)state).lithium$initPathCache();
                    }
                }
            });
        }
    }
}
