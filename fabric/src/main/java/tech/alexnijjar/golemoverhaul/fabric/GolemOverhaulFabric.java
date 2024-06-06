package tech.alexnijjar.golemoverhaul.fabric;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.golems.*;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.tags.ModBiomeTags;

import java.util.function.Predicate;

public class GolemOverhaulFabric {

    public static void init() {
        GolemOverhaul.init();
        ModEntityTypes.registerAttributes((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
        addCustomSpawns();
        addSpawnPlacements();
    }

    public static void addSpawnPlacements() {
        SpawnPlacements.register(ModEntityTypes.BARREL_GOLEM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarrelGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.CANDLE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandleGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.COAL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoalGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.HAY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HayGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.HONEY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HoneyGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.KELP_GOLEM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KelpGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.NETHERITE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.SLIME_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlimeGolem::checkMobSpawnRules);
        SpawnPlacements.register(ModEntityTypes.TERRACOTTA_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracottaGolem::checkMobSpawnRules);
    }

    private static void addCustomSpawns() {
        // TODO: spawn barrel golem in village
        addSpawn(ModEntityTypes.COAL_GOLEM, tag(ModBiomeTags.COAL_GOLEM_SPAWNS), 20, 3, 5);
        addSpawn(ModEntityTypes.HONEY_GOLEM, tag(ModBiomeTags.HONEY_GOLEM_SPAWNS), 20, 1, 2);
        addSpawn(ModEntityTypes.TERRACOTTA_GOLEM, tag(ModBiomeTags.TERRACOTTA_GOLEM_SPAWNS), 20, 1, 2);
        // TODO: spawn hay golem in village
        // TODO: spawn slime golem in slime chunks
    }

    private static Predicate<BiomeSelectionContext> tag(TagKey<Biome> tag) {
        return BiomeSelectors.tag(tag);
    }

    private static <T extends AbstractGolem> void addSpawn(RegistryEntry<EntityType<T>> type, Predicate<BiomeSelectionContext> biomeSelector, int weight, int min, int max) {
        BiomeModifications.addSpawn(
            biomeSelector,
            type.get().getCategory(),
            type.get(), weight,
            min, max);
    }

    public static <T extends AbstractGolem> void addSpawnWithCost(RegistryEntry<EntityType<T>> type, Predicate<BiomeSelectionContext> biomeSelector, int weight, int min, int max, double charge, double energyBudget) {
        BiomeModifications.create(type.getId()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
            context.getSpawnSettings().addSpawn(type.get().getCategory(), new MobSpawnSettings.SpawnerData(type.get(), weight, min, max));
            context.getSpawnSettings().setSpawnCost(type.get(), charge, energyBudget);
        });
    }
}
