package tech.alexnijjar.golemoverhaul.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.NotImplementedException;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.*;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.CandleFlameProjectileRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles.HoneyBlobProjectileRenderer;
import tech.alexnijjar.golemoverhaul.common.constants.ConstantComponents;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.network.NetworkHandler;
import tech.alexnijjar.golemoverhaul.common.network.packets.ServerboundGolemSummonPacket;
import tech.alexnijjar.golemoverhaul.common.registry.ModBlocks;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.function.Supplier;

public class GolemOverhaulClient {

    public static final KeyMapping KEY_NETHERITE_GOLEM_SUMMON = new KeyMapping(
        ConstantComponents.NETHERITE_GOLEM_SUMMON_KEY.getString(),
        InputConstants.KEY_R,
        ConstantComponents.GOLEM_OVERHAUL_CATEGORY.getString());

    public static void init() {
        ClientTickEvent.CLIENT_PRE.register(minecraft -> {
            if (KEY_NETHERITE_GOLEM_SUMMON.consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;
                if (player.getVehicle() instanceof NetheriteGolem) {
                    NetworkHandler.CHANNEL.sendToServer(new ServerboundGolemSummonPacket());
                }
            }
        });

        KeyMappingRegistry.register(KEY_NETHERITE_GOLEM_SUMMON);

        registerEntityRenderers();
        registerBlockRenderTypes();
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntityTypes.BARREL_GOLEM, BarrelGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.CANDLE_GOLEM, CandleGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.COAL_GOLEM, CoalGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.HAY_GOLEM, HayGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.HONEY_GOLEM, HoneyGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.KELP_GOLEM, KelpGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.NETHERITE_GOLEM, NetheriteGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.SLIME_GOLEM, SlimeGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.TERRACOTTA_GOLEM, TerracottaGolemRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.CANDLE_FLAME, CandleFlameProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.MUD_BALL, context ->
                new GeoEntityRenderer<>(context, new DefaultedEntityGeoModel<>(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.MUD_BALL.get()))));
        EntityRendererRegistry.register(ModEntityTypes.HONEY_BLOB, HoneyBlobProjectileRenderer::new);
    }

    public static void registerBlockRenderTypes() {
        registerBlockRenderType(ModBlocks.CANDLE_GOLEM_BLOCK, RenderType.cutout());
        registerBlockRenderType(ModBlocks.CLAY_GOLEM_STATUE, RenderType.cutout());
    }

    @SuppressWarnings("unused")
    @ExpectPlatform
    private static void registerBlockRenderType(Supplier<Block> block, RenderType type) {
        throw new NotImplementedException();
    }
}
