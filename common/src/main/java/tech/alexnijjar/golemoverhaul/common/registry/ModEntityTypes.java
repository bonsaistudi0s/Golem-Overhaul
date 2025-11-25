package tech.alexnijjar.golemoverhaul.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.commons.lang3.NotImplementedException;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.golems.*;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.HoneyBlobProjectile;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.MudBallProjectile;
import tech.alexnijjar.golemoverhaul.common.tags.ModBiomeTags;

public class ModEntityTypes {

    public static final ResourcefulRegistry<EntityType<?>> ENTITY_TYPES =
            ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, GolemOverhaul.MOD_ID);
    public static final ResourcefulRegistry<EntityType<?>> GOLEMS = ResourcefulRegistries.create(ENTITY_TYPES);
    public static final ResourcefulRegistry<EntityType<?>> PROJECTILES = ResourcefulRegistries.create(ENTITY_TYPES);

    public static final RegistryEntry<EntityType<BarrelGolem>> BARREL_GOLEM = GOLEMS.register("barrel_golem", () ->
            createEntityBuilder(BarrelGolem::new, MobCategory.CREATURE)
                    .sized(1, 1)
                    .clientTrackingRange(10)
                    .build("barrel_golem"));

    public static final RegistryEntry<EntityType<CandleGolem>> CANDLE_GOLEM = GOLEMS.register("candle_golem", () ->
            createEntityBuilder(CandleGolem::new, MobCategory.CREATURE)
                    .sized(0.375f, 0.5625f)
                    .clientTrackingRange(10)
                    .build("candle_golem"));

    public static final RegistryEntry<EntityType<CoalGolem>> COAL_GOLEM = GOLEMS.register("coal_golem", () ->
            createEntityBuilder(CoalGolem::new, MobCategory.AMBIENT)
                    .sized(0.25f, 0.5f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("coal_golem"));

    public static final RegistryEntry<EntityType<HayGolem>> HAY_GOLEM = GOLEMS.register("hay_golem", () ->
            createEntityBuilder(HayGolem::new, MobCategory.CREATURE)
                    .sized(0.75f, 2)
                    .clientTrackingRange(10)
                    .build("hay_golem"));

    public static final RegistryEntry<EntityType<HoneyGolem>> HONEY_GOLEM = GOLEMS.register("honey_golem", () ->
            createEntityBuilder(HoneyGolem::new, MobCategory.CREATURE)
                    .sized(0.875f, 1)
                    .clientTrackingRange(10)
                    .build("honey_golem"));

    public static final RegistryEntry<EntityType<KelpGolem>> KELP_GOLEM = GOLEMS.register("kelp_golem", () ->
            createEntityBuilder(KelpGolem::new, MobCategory.CREATURE)
                    .sized(0.8125f, 1.25f)
                    .clientTrackingRange(10)
                    .build("kelp_golem"));

    public static final RegistryEntry<EntityType<NetheriteGolem>> NETHERITE_GOLEM = GOLEMS.register("netherite_golem"
            , () ->
            createEntityBuilder(NetheriteGolem::new, MobCategory.CREATURE)
                    .sized(1.75f, 2.1f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build("netherite_golem"));

    public static final RegistryEntry<EntityType<SlimeGolem>> SLIME_GOLEM = GOLEMS.register("slime_golem", () ->
            createEntityBuilder(SlimeGolem::new, MobCategory.MONSTER)
                    .sized(1.125f, 0.8125f)
                    .clientTrackingRange(10)
                    .build("slime_golem"));

    public static final RegistryEntry<EntityType<TerracottaGolem>> TERRACOTTA_GOLEM = GOLEMS.register(
            "terracotta_golem", () ->
            createEntityBuilder(TerracottaGolem::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.75f)
                    .clientTrackingRange(10)
                    .build("terracotta_golem"));

    public static final RegistryEntry<EntityType<CandleFlameProjectile>> CANDLE_FLAME = PROJECTILES.register(
            "candle_flame", () ->
            createEntityBuilder((EntityType.EntityFactory<CandleFlameProjectile>) CandleFlameProjectile::new,
                    MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("candle_flame"));

    public static final RegistryEntry<EntityType<MudBallProjectile>> MUD_BALL = PROJECTILES.register("mud_ball", () ->
            createEntityBuilder((EntityType.EntityFactory<MudBallProjectile>) MudBallProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("mud_ball"));

    public static final RegistryEntry<EntityType<HoneyBlobProjectile>> HONEY_BLOB = PROJECTILES.register("honey_blob"
            , () ->
            createEntityBuilder((EntityType.EntityFactory<HoneyBlobProjectile>) HoneyBlobProjectile::new,
                    MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("honey_blob"));

    @SuppressWarnings("unused")
    @ExpectPlatform
    public static <T extends Entity> PlatformEntityBuilder<T> createEntityBuilder(EntityType.EntityFactory<T> factory
            , MobCategory category) {
        throw new NotImplementedException();
    }

    public interface PlatformEntityBuilder<T extends Entity> {
        PlatformEntityBuilder<T> sized(float width, float height);

        PlatformEntityBuilder<T> clientTrackingRange(int range);

        PlatformEntityBuilder<T> updateInterval(int interval);

        PlatformEntityBuilder<T> fireImmune();

        EntityType<T> build(String id);
    }

    public static void init() {
        ENTITY_TYPES.init();
        registerAttributes();
        registerSpawnPlacements();
        registerBiomeModifications();
    }

    private static void registerAttributes() {
        EntityAttributeRegistry.register(BARREL_GOLEM, BarrelGolem::createAttributes);
        EntityAttributeRegistry.register(CANDLE_GOLEM, CandleGolem::createAttributes);
        EntityAttributeRegistry.register(COAL_GOLEM, CoalGolem::createAttributes);
        EntityAttributeRegistry.register(HAY_GOLEM, HayGolem::createAttributes);
        EntityAttributeRegistry.register(HONEY_GOLEM, HoneyGolem::createAttributes);
        EntityAttributeRegistry.register(KELP_GOLEM, KelpGolem::createAttributes);
        EntityAttributeRegistry.register(NETHERITE_GOLEM, NetheriteGolem::createAttributes);
        EntityAttributeRegistry.register(SLIME_GOLEM, SlimeGolem::createAttributes);
        EntityAttributeRegistry.register(TERRACOTTA_GOLEM, TerracottaGolem::createAttributes);
    }

    private static void registerSpawnPlacements() {
        SpawnPlacementsRegistry.register(ModEntityTypes.BARREL_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarrelGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.CANDLE_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandleGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.COAL_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoalGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.HAY_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HayGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.HONEY_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HoneyGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.KELP_GOLEM, SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KelpGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.NETHERITE_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteGolem::checkMobSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.SLIME_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlimeGolem::checkSlimeSpawnRules);
        SpawnPlacementsRegistry.register(ModEntityTypes.TERRACOTTA_GOLEM, SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracottaGolem::checkMobSpawnRules);
    }

    private static void registerBiomeModifications() {
        addSpawn(ModEntityTypes.COAL_GOLEM, ModBiomeTags.COAL_GOLEM_SPAWNS, 20, 3, 5);
        addSpawn(ModEntityTypes.HONEY_GOLEM, ModBiomeTags.HONEY_GOLEM_SPAWNS, 6, 1, 2);
        addSpawn(ModEntityTypes.SLIME_GOLEM, ModBiomeTags.SLIME_GOLEM_SPAWNS, 25, 1, 2);
        addSpawn(ModEntityTypes.SLIME_GOLEM, ModBiomeTags.SLIME_GOLEM_SWAMP_SPAWNS, 1, 1, 1);
        addSpawn(ModEntityTypes.TERRACOTTA_GOLEM, ModBiomeTags.TERRACOTTA_GOLEM_SPAWNS, 6, 1, 2);
    }

    private static <T extends Entity> void addSpawn(RegistryEntry<EntityType<T>> entityType,
                                                    TagKey<Biome> spawnBiomesTag, int weight, int minCount,
                                                    int maxCount) {
        BiomeModifications.addProperties(
                biomeContext -> biomeContext.hasTag(spawnBiomesTag),
                (context, properties) -> properties.getSpawnProperties().addSpawn(
                        entityType.get().getCategory(),
                        new MobSpawnSettings.SpawnerData(
                                entityType.get(),
                                weight,
                                minCount,
                                maxCount
                        )
                )
        );
    }
}