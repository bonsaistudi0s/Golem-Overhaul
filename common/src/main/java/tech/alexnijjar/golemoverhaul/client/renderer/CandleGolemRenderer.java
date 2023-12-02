package tech.alexnijjar.golemoverhaul.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.candle.CandleGolem;

import java.util.Objects;

public class CandleGolemRenderer<T extends CandleGolem> extends BaseGolemEntityRenderer<T> {

    public CandleGolemRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem) {
        super(renderManager, golem, CANDLE_GOLEM_ANIMATION);

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(T animatable) {
                return RenderType.eyes(Objects.requireNonNull(glow));
            }

            @Override
            public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if (animatable.isLit()) {
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }
}
