package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.CoalGolem;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModEntityTypes {
    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, GolemOverhaul.MOD_ID);

    public static final RegistryEntry<EntityType<NetheriteGolem>> NETHERITE_GOLEM = ENTITY_TYPES.register("netherite_golem", () ->
        EntityType.Builder.of(NetheriteGolem::new, MobCategory.MISC)
            .sized(1.75f, 2.1f)
            .clientTrackingRange(10)
            .fireImmune()
            .build("netherite_golem"));

    public static final RegistryEntry<EntityType<CoalGolem>> COAL_GOLEM = ENTITY_TYPES.register("coal_golem", () ->
        EntityType.Builder.of(CoalGolem::new, MobCategory.MISC)
            .sized(0.23f, 0.34f)
            .clientTrackingRange(10)
            .fireImmune()
            .build("coal_golem"));

    public static void registerAttributes(BiConsumer<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> attributes) {
        attributes.accept(NETHERITE_GOLEM, NetheriteGolem::createAttributes);
        attributes.accept(COAL_GOLEM, CoalGolem::createAttributes);
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(NETHERITE_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(COAL_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }
}