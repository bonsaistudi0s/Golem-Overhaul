package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.SlimeGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class SlimeGolemRenderer extends BaseGolemRenderer<SlimeGolem> {

    public static final ResourceLocation LARGE_TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/slime/slime_golem.png");
    public static final ResourceLocation SMALL_TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/slime/small_slime_golem.png");

    public static final ResourceLocation LARGE_MODEL = new ResourceLocation(GolemOverhaul.MOD_ID, "geo/entity/slime/slime_golem.geo.json");
    public static final ResourceLocation SMALL_MODEL = new ResourceLocation(GolemOverhaul.MOD_ID, "geo/entity/slime/small_slime_golem.geo.json");

    public SlimeGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.SLIME_GOLEM, false, 0) {
            @Override
            public ResourceLocation getModelResource(SlimeGolem golem) {
                return golem.getSize().isLarge() ? LARGE_MODEL : SMALL_MODEL;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeGolem golem) {
        return golem.getSize().isLarge() ? LARGE_TEXTURE : SMALL_TEXTURE;
    }

    @Override
    public @Nullable RenderType getRenderType(SlimeGolem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SlimeGolem golem, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        boolean isInnerBodyBone = "body_2".equals(bone.getName());
        if (isInnerBodyBone) {
            float healthPercent = 0.5f + Math.clamp((golem.getHealth() / 2) / golem.getMaxHealth(), 0, 0.5f);
            poseStack.pushPose();
            RenderUtil.translateToPivotPoint(poseStack, bone);
            poseStack.scale(healthPercent, healthPercent, healthPercent);
            RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        }
        super.renderRecursively(poseStack, golem, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (isInnerBodyBone) {
            poseStack.popPose();
        }
    }
}
