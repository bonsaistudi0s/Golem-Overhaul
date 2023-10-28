package tech.alexnijjar.golemoverhaul.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import tech.alexnijjar.golemoverhaul.client.renderer.NetheriteGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.NetheriteGolem;

public class NetheriteGolemFireLayer extends GeoRenderLayer<NetheriteGolem> {
    public NetheriteGolemFireLayer(GeoRenderer<NetheriteGolem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, NetheriteGolem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isCharged()) return;
        int summoningTicks = animatable.getSummoningTicks();
        boolean openFlame = summoningTicks <= 43 && summoningTicks > 10;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(openFlame ?
            NetheriteGolemRenderer.CHARGED_OVERLAY_OPEN :
            NetheriteGolemRenderer.CHARGED_OVERLAY));

        getRenderer().reRender(
            getDefaultBakedModel(animatable),
            poseStack, bufferSource, animatable,
            renderType, vertexConsumer,
            partialTick,
            packedLight, packedOverlay,
            1, 1, 1, 1
        );
    }
}
