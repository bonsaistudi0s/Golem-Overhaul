package tech.alexnijjar.golemoverhaul.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import tech.alexnijjar.golemoverhaul.client.renderer.CoalGolemRenderer;
import tech.alexnijjar.golemoverhaul.client.renderer.NetheriteGolemRenderer;
import tech.alexnijjar.golemoverhaul.client.utils.ClientPlatformUtils;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.network.messages.ServerboundGolemSummonPacket;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.function.BiConsumer;

public class GolemOverhaulClient {
    public static final KeyMapping KEY_NETHERITE_GOLEM_SUMMON = new KeyMapping(
        ConstantComponents.NETHERITE_GOLEM_SUMMON_KEY.getString(),
        InputConstants.KEY_R,
        ConstantComponents.GOLEM_OVERHAUL_CATEGORY.getString());

    public static void init() {
        registerEntityRenderers();
    }

    private static void registerEntityRenderers() {
        ClientPlatformUtils.registerRenderer(ModEntityTypes.NETHERITE_GOLEM, NetheriteGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.COAL_GOLEM, CoalGolemRenderer::new);
    }

    public static void onRegisterParticles(BiConsumer<ParticleType<SimpleParticleType>, ClientPlatformUtils.SpriteParticleRegistration<SimpleParticleType>> register) {
    }

    public static void clientTick() {
        if (KEY_NETHERITE_GOLEM_SUMMON.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.getVehicle() instanceof NetheriteGolem) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundGolemSummonPacket());
            }
        }
    }
}
