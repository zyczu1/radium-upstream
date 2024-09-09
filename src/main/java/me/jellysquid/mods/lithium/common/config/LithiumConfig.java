package me.jellysquid.mods.lithium.common.config;

import me.jellysquid.mods.lithium.common.LithiumMod;
import me.jellysquid.mods.lithium.common.compat.worldedit.WorldEditCompat;
import net.caffeinemc.caffeineconfig.AbstractCaffeineConfigMixinPlugin;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import net.caffeinemc.caffeineconfig.Option;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class LithiumConfig extends AbstractCaffeineConfigMixinPlugin {

    private CaffeineConfig applyLithiumCompat(CaffeineConfig config) {
        if (LoadingModList.get().getModFileById("ferritecore") != null) { // https://github.com/malte0811/FerriteCore/blob/1.21.0/Fabric/src/main/resources/fabric.mod.json#L38-L40
            config.getOption("mixin.alloc.blockstate").addModOverride(false, "ferritecore");
        }

        if (LoadingModList.get().getModFileById("moonrise") != null) { // https://github.com/Tuinity/Moonrise/blob/master/fabric/src/main/resources/fabric.mod.json#L43-L69
            config.getOption("mixin.collections.chunk_tickets").addModOverride(false, "moonrise");
            config.getOption("mixin.world.temperature_cache").addModOverride(false, "moonrise");
            config.getOption("mixin.block.flatten_states").addModOverride(false, "moonrise");
            config.getOption("mixin.math.fast_util").addModOverride(false, "moonrise");
            config.getOption("mixin.minimal_nonvanilla.collisions.empty_space").addModOverride(false, "moonrise");
            config.getOption("mixin.alloc.deep_passengers").addModOverride(false, "moonrise");
            config.getOption("mixin.math.fast_blockpos").addModOverride(false, "moonrise");
            config.getOption("mixin.shapes.blockstate_cache").addModOverride(false, "moonrise");
            config.getOption("mixin.world.block_entity_ticking").addModOverride(false, "moonrise");
            config.getOption("mixin.collections.entity_ticking").addModOverride(false, "moonrise");
            config.getOption("mixin.world.chunk_access").addModOverride(false, "moonrise");
            config.getOption("mixin.ai.poi").addModOverride(false, "moonrise");
            config.getOption("mixin.chunk.no_validation").addModOverride(false, "moonrise");
            config.getOption("mixin.minimal_nonvanilla.world.expiring_chunk_tickets").addModOverride(false, "moonrise");
            config.getOption("mixin.world.tick_scheduler").addModOverride(false, "moonrise");
            config.getOption("mixin.alloc.chunk_ticking").addModOverride(false, "moonrise");
            config.getOption("mixin.entity.replace_entitytype_predicates").addModOverride(false, "moonrise");
            config.getOption("mixin.util.block_tracking").addModOverride(false, "moonrise");
            config.getOption("mixin.shapes.specialized_shapes").addModOverride(false, "moonrise");
            config.getOption("mixin.shapes.optimized_matching").addModOverride(false, "moonrise");
            config.getOption("mixin.entity.collisions.intersection").addModOverride(false, "moonrise");
            config.getOption("mixin.entity.collisions.movement").addModOverride(false, "moonrise");
            config.getOption("mixin.entity.collisions.unpushable_cramming").addModOverride(false, "moonrise");
            config.getOption("mixin.chunk.entity_class_groups").addModOverride(false, "moonrise");
            config.getOption("mixin.alloc.entity_tracker").addModOverride(false, "moonrise");

            // Additional
            config.getOption("mixin.entity.collisions.fluid").addModOverride(false, "moonrise");
            config.getOption("mixin.world.explosions").addModOverride(false, "moonrise");
        }

        Option option = config.getOption("mixin.block.hopper.worldedit_compat");
        if (!option.isEnabled() && WorldEditCompat.WORLD_EDIT_PRESENT) {
            option.addModOverride(true, "radium");
        }

        return config;
    }

    public LithiumConfig() {
        super();

        LithiumMod.CONFIG = this;
    }

    @Override
    protected CaffeineConfig createConfig() {
        CaffeineConfig.Builder builder = CaffeineConfig.builder("Radium")
                .withInfoUrl("https://github.com/jellysquid3/lithium-fabric/wiki/Configuration-File")
                .withSettingsKey("lithium:options");

        // Defines the default rules which can be configured by the user or other mods.
        InputStream defaultPropertiesStream = LithiumConfig.class.getResourceAsStream("/assets/lithium/lithium-mixin-config-default.properties");
        if (defaultPropertiesStream == null) {
            throw new IllegalStateException("Lithium mixin config default properties could not be read!");
        }
        try (BufferedReader propertiesReader = new BufferedReader(new InputStreamReader(defaultPropertiesStream))) {
            Properties properties = new Properties();
            properties.load(propertiesReader);
            properties.forEach((ruleName, enabled) -> builder.addMixinRule((String) ruleName, Boolean.parseBoolean((String) enabled)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Lithium mixin config default properties could not be read!");
        }
        InputStream dependenciesStream = LithiumConfig.class.getResourceAsStream("/assets/lithium/lithium-mixin-config-dependencies.properties");
        if (dependenciesStream == null) {
            throw new IllegalStateException("Lithium mixin config dependencies could not be read!");
        }
        try (BufferedReader propertiesReader = new BufferedReader(new InputStreamReader(dependenciesStream))) {
            Properties properties = new Properties();
            properties.load(propertiesReader);
            properties.forEach(
                    (o1, o2) -> {
                        String rulename = (String) o1;
                        String dependencies = (String) o2;
                        String[] dependenciesSplit = dependencies.split(",");
                        for (String dependency : dependenciesSplit) {
                            String[] split = dependency.split(":");
                            if (split.length != 2) {
                                return;
                            }
                            String dependencyName = split[0];
                            String requiredState = split[1];
                            builder.addRuleDependency(rulename, dependencyName, Boolean.parseBoolean(requiredState));
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Lithium mixin config dependencies could not be read!");
        }

        return applyLithiumCompat(builder.build(FMLPaths.CONFIGDIR.get().resolve("lithium.properties")));
    }

    @Override
    protected String mixinPackageRoot() {
        return "me.jellysquid.mods.lithium.mixin.";
    }
}
