package tech.alexnijjar.golemoverhaul;

import com.teamresourceful.resourcefulconfig.common.config.Configurator;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import tech.alexnijjar.golemoverhaul.client.GolemOverhaulClient;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.common.entities.IShearable;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HayGolem;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;
import tech.alexnijjar.golemoverhaul.common.events.ModEvents;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.registry.*;

import java.util.Random;

public final class GolemOverhaul {

    public static final String MOD_ID = "golemoverhaul";
    public static final Configurator CONFIGURATOR = new Configurator();

    public static void init() {
        CONFIGURATOR.registerConfig(GolemOverhaulConfig.class);

        NetworkHandler.init();
        ModEntityTypes.init();
        ModBlocks.BLOCKS.init();
        ModItems.ITEMS.init();
        ModParticleTypes.PARTICLE_TYPES.init();
        ModSoundEvents.SOUND_EVENTS.init();
        ModRecipeTypes.RECIPE_TYPES.init();
        ModRecipeSerializers.RECIPE_SERIALIZERS.init();
        ModEvents.init();

        if (Platform.getEnvironment() == Env.CLIENT) {
            GolemOverhaulClient.init();
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
