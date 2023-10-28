package tech.alexnijjar.golemoverhaul.fabric;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.function.Predicate;

public class GolemOverhaulFabric {

    public static void init() {
        GolemOverhaul.init();
        ModEntityTypes.registerAttributes((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
        addCustomSpawns();
    }

    private static void addCustomSpawns() {
    }

    private static Predicate<BiomeSelectionContext> tag(TagKey<Biome> tag) {
        return BiomeSelectors.tag(tag);
    }

    private static <T extends IronGolem> void addSpawn(RegistryEntry<EntityType<T>> type, TagKey<Biome> tag) {
        addSpawn(type, tag(tag), 10, 1, 4);
    }

    private static <T extends IronGolem> void addSpawn(RegistryEntry<EntityType<T>> type, Predicate<BiomeSelectionContext> biomeSelector, int weight, int min, int max) {
        BiomeModifications.addSpawn(
            biomeSelector,
            type.get().getCategory(),
            type.get(), weight,
            min, max);
    }

    public static <T extends IronGolem> void addSpawnWithCost(RegistryEntry<EntityType<T>> type, Predicate<BiomeSelectionContext> biomeSelector, int weight, int min, int max, double charge, double energyBudget) {
        BiomeModifications.create(type.getId()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
            context.getSpawnSettings().addSpawn(type.get().getCategory(), new MobSpawnSettings.SpawnerData(type.get(), weight, min, max));
            context.getSpawnSettings().setSpawnCost(type.get(), charge, energyBudget);
        });
    }
}
