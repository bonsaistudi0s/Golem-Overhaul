package tech.alexnijjar.golemoverhaul.fabric;

import net.fabricmc.api.ModInitializer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;

@SuppressWarnings("unused")
public final class GolemOverhaulFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GolemOverhaul.init();
    }
}
