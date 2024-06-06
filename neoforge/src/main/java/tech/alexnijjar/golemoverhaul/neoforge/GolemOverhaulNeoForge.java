package tech.alexnijjar.golemoverhaul.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.neoforge.GolemOverhaulClientNeoForge;
import tech.alexnijjar.golemoverhaul.common.entities.golems.*;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@Mod(GolemOverhaul.MOD_ID)
public class GolemOverhaulNeoForge {

    public GolemOverhaulNeoForge(IEventBus bus) {
        GolemOverhaul.init();
        bus.addListener(GolemOverhaulNeoForge::onAttributes);
        bus.addListener(GolemOverhaulNeoForge::commonSetup);
        bus.addListener(GolemOverhaulNeoForge::onRegisterSpawnPlacements);
        if (FMLEnvironment.dist.isClient()) {
            GolemOverhaulClientNeoForge.init();
        }
        NeoForge.EVENT_BUS.addListener(GolemOverhaulNeoForge::onFarmlandTrample);
        NeoForge.EVENT_BUS.addListener(GolemOverhaulNeoForge::onBlockPlace);
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        ModEntityTypes.registerAttributes((entityType, attribute) -> event.put(entityType.get(), attribute.get().build()));
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        GolemOverhaul.postInit();
    }

    private static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        LevelAccessor level = event.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = event.getPos();
            AABB bounds = event.getState().getCollisionShape(level, pos).bounds().move(pos).inflate(10);

            if (!level.getEntitiesOfClass(HayGolem.class, bounds).isEmpty()) {
                event.setCanceled(true);
            }
        }
    }

    private static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (event.getState().is(Blocks.SEA_LANTERN)) KelpGolem.trySpawnGolem(level, event.getPos());
            if (event.getState().is(Blocks.DRIED_KELP_BLOCK)) KelpGolem.trySpawnGolem(level, event.getPos());
        }
    }

    public static void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntityTypes.BARREL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BarrelGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.CANDLE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandleGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.COAL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoalGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.HAY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HayGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.HONEY_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HoneyGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.KELP_GOLEM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KelpGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.NETHERITE_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.SLIME_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlimeGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(ModEntityTypes.TERRACOTTA_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TerracottaGolem::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
