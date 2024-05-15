package tech.alexnijjar.golemoverhaul.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.*;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.CandleFlameProjectileRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.HoneyBlobProjectileRenderer;
import tech.alexnijjar.golemoverhaul.client.utils.ClientPlatformUtils;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.network.packets.ServerboundGolemSummonPacket;
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

    @SuppressWarnings("UnreachableCode")
    private static void registerEntityRenderers() {
        ClientPlatformUtils.registerRenderer(ModEntityTypes.BARREL_GOLEM, BarrelGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.CANDLE_GOLEM, CandleGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.COAL_GOLEM, CoalGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.HAY_GOLEM, HayGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.HONEY_GOLEM, HoneyGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.KELP_GOLEM, KelpGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.NETHERITE_GOLEM, NetheriteGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.SLIME_GOLEM, SlimeGolemRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.TERRACOTTA_GOLEM, TerracottaGolemRenderer::new);

        ClientPlatformUtils.registerRenderer(ModEntityTypes.CANDLE_FLAME, CandleFlameProjectileRenderer::new);
        ClientPlatformUtils.registerRenderer(ModEntityTypes.MUD_BALL, context ->
            new GeoEntityRenderer<>(context, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.MUD_BALL.get()))));
        ClientPlatformUtils.registerRenderer(ModEntityTypes.HONEY_BLOB, HoneyBlobProjectileRenderer::new);
    }

    public static void onRegisterParticles(BiConsumer<ParticleType<SimpleParticleType>, ClientPlatformUtils.SpriteParticleRegistration<SimpleParticleType>> consumer) {
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
