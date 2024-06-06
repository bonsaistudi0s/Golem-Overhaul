package tech.alexnijjar.golemoverhaul.client.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import tech.alexnijjar.golemoverhaul.client.GolemOverhaulClient;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class GolemOverhaulClientNeoForge {

    public static void init() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(GolemOverhaulClient::init);
        NeoForge.EVENT_BUS.addListener(GolemOverhaulClientNeoForge::onClientTick);
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        GolemOverhaulClient.onRegisterParticles((type, provider) -> event.registerSpriteSet(type, provider::create));
    }

    @SubscribeEvent
    public static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(GolemOverhaulClient.KEY_NETHERITE_GOLEM_SUMMON);
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        GolemOverhaulClient.clientTick();
    }
}
