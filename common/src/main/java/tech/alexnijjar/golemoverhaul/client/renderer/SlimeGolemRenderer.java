package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.slime.SlimeGolem;

public class SlimeGolemRenderer <T extends SlimeGolem> extends BaseGolemEntityRenderer<T> {
    public SlimeGolemRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem, ResourceLocation animation) {
        super(renderManager, golem, animation);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
