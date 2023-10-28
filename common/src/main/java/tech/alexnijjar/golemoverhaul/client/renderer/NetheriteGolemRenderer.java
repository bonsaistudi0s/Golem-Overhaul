package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderer.layers.NetheriteGolemFireLayer;
import tech.alexnijjar.golemoverhaul.client.renderer.layers.NetheriteGolemGoldLayer;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class NetheriteGolemRenderer extends BaseGolemEntityRenderer<NetheriteGolem> {
    public static final ResourceLocation GOLD_OVERLAY = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_gold_overlay.png");
    public static final ResourceLocation CHARGED_OVERLAY = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_charged_glow.png");
    public static final ResourceLocation CHARGED_OVERLAY_OPEN = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_charged_glow_open.png");

    public NetheriteGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(
            BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.NETHERITE_GOLEM.get()),
            true,
            getTexture(ModEntityTypes.NETHERITE_GOLEM.get()),
            NETHERITE_GOLEM_ANIMATION, 10));
        withScale(1.2f);

        addRenderLayer(new NetheriteGolemGoldLayer(this));
        addRenderLayer(new NetheriteGolemFireLayer(this));
    }
}
