package tech.alexnijjar.golemoverhaul.fabric.client;

import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.config.GolemOverhaulConfig;

@SuppressWarnings("unused")
public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var config = GolemOverhaul.CONFIGURATOR.getConfig(GolemOverhaulConfig.class);
            if (config == null) {
                return null;
            }

            return new ConfigScreen(parent, null, config);
        };
    }
}
