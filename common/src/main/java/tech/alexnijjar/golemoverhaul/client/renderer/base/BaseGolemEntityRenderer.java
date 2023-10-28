package tech.alexnijjar.golemoverhaul.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;

public class BaseGolemEntityRenderer<T extends BaseGolem> extends GeoEntityRenderer<T> {
    public static final ResourceLocation ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "golem");
    public static final ResourceLocation NETHERITE_GOLEM_ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "netherite_golem");

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> enderman) {
        this(renderManager, enderman, ANIMATION);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> enderman, ResourceLocation animation) {
        this(renderManager, enderman, animation, true);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> enderman, ResourceLocation animation, boolean turnsHead) {
        this(renderManager,
            BuiltInRegistries.ENTITY_TYPE.getKey(enderman),
            getTexture(enderman),
            animation,
            getGlowTexture(enderman),
            turnsHead);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, ResourceLocation assetPath, ResourceLocation texture, ResourceLocation animation, ResourceLocation glow, boolean turnsHead) {
        super(renderManager, new BaseGolemModel<>(assetPath, turnsHead, texture, animation, 90));
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    public static ResourceLocation getTexture(EntityType<?> enderman) {
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(enderman).getPath();
        return new ResourceLocation(GolemOverhaul.MOD_ID, "%s/%s".formatted(name.replace("_golem", ""), name));
    }

    public static ResourceLocation getGlowTexture(EntityType<?> enderman) {
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(enderman).getPath();
        return new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/%s/%s_glow.png".formatted(name.replace("_golem", ""), name));
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        if (!animatable.hasCustomDeathAnimation()) {
            super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
            return;
        }

        int deathTime = animatable.deathTime;
        animatable.deathTime = 0;
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        animatable.deathTime = deathTime;
    }
}
