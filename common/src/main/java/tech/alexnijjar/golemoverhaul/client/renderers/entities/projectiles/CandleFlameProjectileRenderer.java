package tech.alexnijjar.golemoverhaul.client.renderers.entities.projectiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import tech.alexnijjar.golemoverhaul.common.entities.projectiles.CandleFlameProjectile;

public class CandleFlameProjectileRenderer extends EntityRenderer<CandleFlameProjectile> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/flame.png");

    public CandleFlameProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CandleFlameProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        this.renderQuad(poseStack.last().pose());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderQuad(Matrix4f matrix) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(matrix, -0.25f, -0.4f, 0).uv(1, 0).endVertex();
        buffer.vertex(matrix, 0.25f, -0.4f, 0).uv(0, 0).endVertex();
        buffer.vertex(matrix, 0.25f, 0.1f, 0).uv(0, 1).endVertex();
        buffer.vertex(matrix, -0.25f, 0.1f, 0).uv(1, 1).endVertex();

        tesselator.end();
    }

    @Override
    public ResourceLocation getTextureLocation(CandleFlameProjectile entity) {
        return TEXTURE;
    }
}
