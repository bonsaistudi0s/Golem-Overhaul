package tech.alexnijjar.golemoverhaul.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.forge.client.GolemOverhaulForgeClient;

@SuppressWarnings("removal")
@Mod(GolemOverhaul.MOD_ID)
public final class GolemOverhaulForge {

    public GolemOverhaulForge() {
        EventBuses.registerModEventBus(GolemOverhaul.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        GolemOverhaul.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            GolemOverhaulForgeClient.init();
        }
    }
}
