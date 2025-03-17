package tech.alexnijjar.golemoverhaul;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import tech.alexnijjar.golemoverhaul.client.GolemOverhaulClient;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.registry.*;

@Mod(GolemOverhaul.MOD_ID)
public class GolemOverhaul {

    public static final String MOD_ID = "golemoverhaul";
    public static final Configurator CONFIGURATOR = new Configurator(MOD_ID);

    public GolemOverhaul(IEventBus bus) {
        CONFIGURATOR.register(GolemOverhaulConfig.class);

        NetworkHandler.init();
        ModBlocks.BLOCKS.init();
        ModItems.ITEMS.init();
        ModItems.TABS.init();
        ModEntityTypes.ENTITY_TYPES.init();
        ModParticleTypes.PARTICLE_TYPES.init();
        ModSoundEvents.SOUND_EVENTS.init();
        ModRecipeTypes.RECIPE_TYPES.init();
        ModRecipeSerializers.RECIPE_SERIALIZERS.init();
        NeoForge.EVENT_BUS.addListener(GolemOverhaulClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(GolemOverhaul::onBlockPlace);
    }

    private static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof Level level && (event.getPlacedBlock().is(Blocks.DRIED_KELP_BLOCK) || event.getPlacedBlock().is(Blocks.SEA_LANTERN))) {
            KelpGolem.trySpawnGolem(level, event.getPos());
        }
    }
}
