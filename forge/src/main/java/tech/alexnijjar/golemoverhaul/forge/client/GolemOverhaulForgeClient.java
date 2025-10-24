package tech.alexnijjar.golemoverhaul.forge.client;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;

public class GolemOverhaulForgeClient {

    public static void init(FMLJavaModLoadingContext context) {
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
                    var config = GolemOverhaul.CONFIGURATOR.getConfig(GolemOverhaulConfig.class);
                    if (config == null) return null;
                    return new ConfigScreen(parent, null, config);
                })
        );
    }
}
