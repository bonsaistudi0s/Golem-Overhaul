package tech.alexnijjar.golemoverhaul.forge;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;
import tech.alexnijjar.golemoverhaul.forge.client.GolemOverhaulForgeClient;

@Mod(GolemOverhaul.MOD_ID)
public final class GolemOverhaulForge {

    public GolemOverhaulForge(FMLJavaModLoadingContext context) {
        EventBuses.registerModEventBus(GolemOverhaul.MOD_ID, context.getModEventBus());

        GolemOverhaul.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            GolemOverhaulForgeClient.init(context);
        }
    }
}
