package tech.alexnijjar.golemoverhaul;

import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModItems;
import tech.alexnijjar.golemoverhaul.common.registry.ModParticleTypes;
import tech.alexnijjar.golemoverhaul.common.registry.ModSoundEvents;

public class GolemOverhaul {

    public static final String MOD_ID = "golemoverhaul";
    public static final Configurator CONFIGURATOR = new Configurator();

    public static void init() {
        CONFIGURATOR.registerConfig(GolemOverhaulConfig.class);
        NetworkHandler.init();
        ModItems.ITEMS.init();
        ModEntityTypes.ENTITY_TYPES.init();
        ModParticleTypes.PARTICLE_TYPES.init();
        ModSoundEvents.SOUND_EVENTS.init();
    }

    public static void postInit() {
        ModEntityTypes.registerSpawnPlacements();
    }
}
