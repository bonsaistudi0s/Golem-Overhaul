package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.BarrelGolem;

public class BarrelGolemHeldItemLayer extends GeoRenderLayer<BarrelGolem> {

    public BarrelGolemHeldItemLayer(GeoRenderer<BarrelGolem> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, BarrelGolem golem, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!golem.isBartering() || golem.getBarteringTicks() < 34) return;
        if (golem.isDeadOrDying()) return;
        ItemStack stack = golem.getMainHandItem();
        if (stack.isEmpty()) return;

        GeoBone itemBone = getGeoModel().getBone("entity").orElse(null);
        if (itemBone == null) return;

        float lerped = Mth.rotLerp(partialTick, golem.yBodyRotO, golem.yBodyRot);
        try (var pose = new CloseablePoseStack(poseStack)) {
            pose.mulPose(Axis.YP.rotationDegrees(180));
            pose.mulPose(Axis.YP.rotationDegrees(-lerped));

            pose.mulPose(itemBone.getModelRotationMatrix());


            Minecraft.getInstance().getItemRenderer().renderStatic(golem, stack, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, false, pose, bufferSource, golem.level(), packedLight, packedOverlay, golem.getId());
        }
    }
}
