package me.jellysquid.mods.lithium.common.ai.pathing;

import me.jellysquid.mods.lithium.common.reflection.ReflectionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BlockClassChecker {
    private static final Map<Class<?>, Boolean> DYNAMIC_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Function<Class<?>, Boolean> DYNAMIC_TYPE_CHECKER = hasNonstandardImplementation("getBlockPathType", BlockState.class, BlockView.class, BlockPos.class, MobEntity.class);
    private static final Map<Class<?>, Boolean> DYNAMIC_FIRE_CACHE = new ConcurrentHashMap<>();
    private static final Function<Class<?>, Boolean> DYNAMIC_FIRE_CHECKER = hasNonstandardImplementation("isBurning", BlockState.class, BlockView.class, BlockPos.class);

    public static boolean shouldUseDynamicTypeCheck(Class<?> blockClass) {
        return DYNAMIC_TYPE_CACHE.computeIfAbsent(blockClass, DYNAMIC_TYPE_CHECKER);
    }

    public static boolean shouldUseDynamicBurningCheck(Class<?> blockClass) {
        return DYNAMIC_FIRE_CACHE.computeIfAbsent(blockClass, DYNAMIC_FIRE_CHECKER);
    }

    private static Function<Class<?>, Boolean> hasNonstandardImplementation(String name, Class<?>... args) {
        return blockClass -> ReflectionUtil.hasMethodOverride(blockClass, IForgeBlock.class, false, name, args);
    }
}
