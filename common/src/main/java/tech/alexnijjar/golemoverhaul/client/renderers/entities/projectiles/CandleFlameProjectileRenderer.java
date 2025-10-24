package tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;

public class CandleFlameProjectileRenderer extends EntityRenderer<CandleFlameProjectile> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/flame.png");

    public CandleFlameProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull CandleFlameProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        var pose = poseStack.last();
        var matrix4f = pose.pose();
        var matrix3f = pose.normal();

        var vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));

        addVertex(vertexConsumer, matrix4f, matrix3f, -0.25F, -0.1F, 1, 1);
        addVertex(vertexConsumer, matrix4f, matrix3f, 0.25F, -0.1F, 0, 1);
        addVertex(vertexConsumer, matrix4f, matrix3f, 0.25F, 0.4F, 0, 0);
        addVertex(vertexConsumer, matrix4f, matrix3f, -0.25F, 0.4F, 1, 0);

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float u, float v) {
        consumer.vertex(matrix4f, x, y, 0)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CandleFlameProjectile entity) {
        return TEXTURE;
    }
}
