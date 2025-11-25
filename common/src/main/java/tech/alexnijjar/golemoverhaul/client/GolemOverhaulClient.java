package tech.alexnijjar.golemoverhaul.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.*;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.CandleFlameProjectileRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.HoneyBlobProjectileRenderer;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.network.packets.ServerboundGolemSummonPacket;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

@EventBusSubscriber(value = Dist.CLIENT, modid = GolemOverhaul.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class GolemOverhaulClient {

    public static final KeyMapping KEY_NETHERITE_GOLEM_SUMMON = new KeyMapping(
        ConstantComponents.NETHERITE_GOLEM_SUMMON_KEY.getString(),
        InputConstants.KEY_R,
        ConstantComponents.GOLEM_OVERHAUL_CATEGORY.getString());

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.BARREL_GOLEM.get(), BarrelGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CANDLE_GOLEM.get(), CandleGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.COAL_GOLEM.get(), CoalGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.HAY_GOLEM.get(), HayGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.HONEY_GOLEM.get(), HoneyGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.KELP_GOLEM.get(), KelpGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.NETHERITE_GOLEM.get(), NetheriteGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SLIME_GOLEM.get(), SlimeGolemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.TERRACOTTA_GOLEM.get(), TerracottaGolemRenderer::new);

        event.registerEntityRenderer(ModEntityTypes.CANDLE_FLAME.get(), CandleFlameProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MUD_BALL.get(), context ->
            new GeoEntityRenderer<>(context, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.MUD_BALL.get()))));
        event.registerEntityRenderer(ModEntityTypes.HONEY_BLOB.get(), HoneyBlobProjectileRenderer::new);
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        if (KEY_NETHERITE_GOLEM_SUMMON.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.getVehicle() instanceof NetheriteGolem) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundGolemSummonPacket());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(GolemOverhaulClient.KEY_NETHERITE_GOLEM_SUMMON);
    }
}
