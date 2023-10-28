package tech.alexnijjar.golemoverhaul.client.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import tech.alexnijjar.golemoverhaul.client.GolemOverhaulClient;
import tech.alexnijjar.golemoverhaul.client.utils.ClientPlatformUtils;

public class GolemOverhaulClientFabric {

    public static void init() {
        GolemOverhaulClient.init();
        GolemOverhaulClient.onRegisterParticles(GolemOverhaulClientFabric::registerParticles);
        KeyBindingHelper.registerKeyBinding(GolemOverhaulClient.KEY_NETHERITE_GOLEM_SUMMON);
        ClientTickEvents.START_CLIENT_TICK.register(client -> GolemOverhaulClient.clientTick());
    }

    private static void registerParticles(ParticleType<SimpleParticleType> particle, ClientPlatformUtils.SpriteParticleRegistration<SimpleParticleType> provider) {
        ParticleFactoryRegistry.getInstance().register(particle, provider::create);
    }
}
