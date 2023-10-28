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

public class NetheriteGolemGoldLayer extends GeoRenderLayer<NetheriteGolem> {
    public NetheriteGolemGoldLayer(GeoRenderer<NetheriteGolem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, NetheriteGolem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isGilded()) return;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(NetheriteGolemRenderer.GOLD_OVERLAY));
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
