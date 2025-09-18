package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.golems.*;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.MudBallProjectile;

@EventBusSubscriber(modid = GolemOverhaul.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityTypes {

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, GolemOverhaul.MOD_ID);
    public static final ResourcefulRegistry<EntityType<?>> GOLEMS = ResourcefulRegistries.create(ENTITY_TYPES);
    public static final ResourcefulRegistry<EntityType<?>> PROJECTILES = ResourcefulRegistries.create(ENTITY_TYPES);

    public static final RegistryEntry<EntityType<BarrelGolem>> BARREL_GOLEM = GOLEMS.register("barrel_golem", () ->
        EntityType.Builder.of(BarrelGolem::new, MobCategory.CREATURE)
            .sized(1, 1)
            .clientTrackingRange(10)
            .build("barrel_golem"));

    public static final RegistryEntry<EntityType<CandleGolem>> CANDLE_GOLEM = GOLEMS.register("candle_golem", () ->
        EntityType.Builder.of(CandleGolem::new, MobCategory.CREATURE)
            .sized(0.375f, 0.5625f)
            .clientTrackingRange(10)
            .build("candle_golem"));

    public static final RegistryEntry<EntityType<CoalGolem>> COAL_GOLEM = GOLEMS.register("coal_golem", () ->
        EntityType.Builder.of(CoalGolem::new, MobCategory.AMBIENT)
            .sized(0.25f, 0.5f)
            .clientTrackingRange(10)
            .fireImmune()
            .build("coal_golem"));

    public static final RegistryEntry<EntityType<HayGolem>> HAY_GOLEM = GOLEMS.register("hay_golem", () ->
        EntityType.Builder.of(HayGolem::new, MobCategory.CREATURE)
            .sized(0.75f, 2)
            .clientTrackingRange(10)
            .build("hay_golem"));

    public static final RegistryEntry<EntityType<HoneyGolem>> HONEY_GOLEM = GOLEMS.register("honey_golem", () ->
        EntityType.Builder.of(HoneyGolem::new, MobCategory.CREATURE)
            .sized(0.875f, 1)
            .clientTrackingRange(10)
            .build("honey_golem"));

    public static final RegistryEntry<EntityType<KelpGolem>> KELP_GOLEM = GOLEMS.register("kelp_golem", () ->
        EntityType.Builder.of(KelpGolem::new, MobCategory.CREATURE)
            .sized(0.8125f, 1.25f)
            .clientTrackingRange(10)
            .build("kelp_golem"));

    public static final RegistryEntry<EntityType<NetheriteGolem>> NETHERITE_GOLEM = GOLEMS.register("netherite_golem", () ->
        EntityType.Builder.of(NetheriteGolem::new, MobCategory.CREATURE)
            .sized(1.75f, 2.1f)
            .clientTrackingRange(10)
            .fireImmune()
            .build("netherite_golem"));

    public static final RegistryEntry<EntityType<SlimeGolem>> SLIME_GOLEM = GOLEMS.register("slime_golem", () ->
        EntityType.Builder.of(SlimeGolem::new, MobCategory.MONSTER)
            .sized(1.125f, 0.8125f)
            .clientTrackingRange(10)
            .build("slime_golem"));

    public static final RegistryEntry<EntityType<TerracottaGolem>> TERRACOTTA_GOLEM = GOLEMS.register("terracotta_golem", () ->
        EntityType.Builder.of(TerracottaGolem::new, MobCategory.CREATURE)
            .sized(0.5f, 0.75f)
            .clientTrackingRange(10)
            .build("terracotta_golem"));

    public static final RegistryEntry<EntityType<CandleFlameProjectile>> CANDLE_FLAME = PROJECTILES.register("candle_flame", () ->
        EntityType.Builder.<CandleFlameProjectile>of(CandleFlameProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("candle_flame"));

    public static final RegistryEntry<EntityType<MudBallProjectile>> MUD_BALL = PROJECTILES.register("mud_ball", () ->
        EntityType.Builder.<MudBallProjectile>of(MudBallProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("mud_ball"));

    public static final RegistryEntry<EntityType<HoneyBlobProjectile>> HONEY_BLOB = PROJECTILES.register("honey_blob", () ->
        EntityType.Builder.<HoneyBlobProjectile>of(HoneyBlobProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("honey_blob"));

    @SubscribeEvent
    public static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(BARREL_GOLEM.get(), BarrelGolem.createAttributes().build());
        event.put(CANDLE_GOLEM.get(), CandleGolem.createAttributes().build());
        event.put(COAL_GOLEM.get(), CoalGolem.createAttributes().build());
        event.put(HAY_GOLEM.get(), HayGolem.createAttributes().build());
        event.put(HONEY_GOLEM.get(), HoneyGolem.createAttributes().build());
        event.put(KELP_GOLEM.get(), KelpGolem.createAttributes().build());
        event.put(NETHERITE_GOLEM.get(), NetheriteGolem.createAttributes().build());
        event.put(SLIME_GOLEM.get(), SlimeGolem.createAttributes().build());
        event.put(TERRACOTTA_GOLEM.get(), TerracottaGolem.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntityTypes.BARREL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarrelGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.CANDLE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandleGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.COAL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoalGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.HAY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HayGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.HONEY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HoneyGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.KELP_GOLEM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KelpGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.NETHERITE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.SLIME_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlimeGolem::checkSlimeSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ModEntityTypes.TERRACOTTA_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracottaGolem::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}