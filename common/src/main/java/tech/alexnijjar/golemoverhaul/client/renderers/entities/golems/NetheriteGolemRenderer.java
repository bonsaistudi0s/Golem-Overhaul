package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.layers.NetheriteGolemFireLayer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.layers.NetheriteGolemGoldLayer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.NetheriteGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class NetheriteGolemRenderer extends BaseGolemRenderer<NetheriteGolem> {

    public static final ResourceLocation GOLD_OVERLAY = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_gold_overlay.png");
    public static final ResourceLocation CHARGED_OVERLAY = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_charged_glow.png");
    public static final ResourceLocation CHARGED_OVERLAY_OPEN = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/netherite/netherite_golem_charged_glow_open.png");

    public NetheriteGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.NETHERITE_GOLEM, true, 10));
        this.withScale(1.2f); // Joosh said he made the model too small.

        addRenderLayer(new NetheriteGolemGoldLayer(this));
        addRenderLayer(new NetheriteGolemFireLayer(this));
    }

    @Override
    protected void applyRotations(NetheriteGolem golem, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        int deathTime = golem.deathTime;
        golem.deathTime = 0;
        super.applyRotations(golem, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        golem.deathTime = deathTime;
    }
}
