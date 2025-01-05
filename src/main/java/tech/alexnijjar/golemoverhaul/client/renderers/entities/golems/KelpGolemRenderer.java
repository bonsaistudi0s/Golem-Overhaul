package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.KelpGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class KelpGolemRenderer extends BaseGolemRenderer<KelpGolem> {

    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/kelp/kelp_golem_glow.png");

    public KelpGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.KELP_GOLEM, true, 90));

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(KelpGolem animatable, @Nullable MultiBufferSource bufferSource) {
                return RenderType.eyes(GLOW);
            }

            @Override
            public void render(PoseStack poseStack, KelpGolem golem, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                float percent = golem.getHealth() / golem.getMaxHealth();
                int strength = (int) (percent * 255);
                int color = FastColor.ARGB32.color(strength, strength, strength, strength);
                renderType = getRenderType(animatable, bufferSource);
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                    bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    color
                );
            }
        });
    }

    @Override
    public void renderRecursively(PoseStack poseStack, KelpGolem golem, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (("particle".equals(bone.getName()) || "particle2".equals(bone.getName())) && !golem.isCharged()) {
            return;
        }
        super.renderRecursively(poseStack, golem, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
