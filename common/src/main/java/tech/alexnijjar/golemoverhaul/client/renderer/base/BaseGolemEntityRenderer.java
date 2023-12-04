package tech.alexnijjar.golemoverhaul.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;

public class BaseGolemEntityRenderer<T extends BaseGolem> extends GeoEntityRenderer<T> {
    public static final ResourceLocation ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "golem");
    public static final ResourceLocation NETHERITE_GOLEM_ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "netherite_golem");
    public static final ResourceLocation CANDLE_GOLEM_ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "candle_golem");
    public static final ResourceLocation TERRACOTTA_GOLEM_ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "terracotta_golem");
    public static final ResourceLocation HONEY_GOLEM_ANIMATION = new ResourceLocation(GolemOverhaul.MOD_ID, "honey_golem");

    @Nullable
    protected final ResourceLocation glow;

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem) {
        this(renderManager, golem, ANIMATION);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem, ResourceLocation animation) {
        this(renderManager, golem, animation, true);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem, ResourceLocation animation, boolean turnsHead) {
        this(renderManager,
            BuiltInRegistries.ENTITY_TYPE.getKey(golem),
            getTexture(golem),
            animation,
            getGlowTexture(golem),
            turnsHead);
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, ResourceLocation assetPath, ResourceLocation texture, ResourceLocation animation, ResourceLocation glow, boolean turnsHead) {
        super(renderManager, new BaseGolemModel<>(assetPath, turnsHead, texture, animation, 90));
        this.glow = glow;
    }

    public BaseGolemEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.glow = null;
    }

    public static ResourceLocation getTexture(EntityType<?> golem) {
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(golem).getPath();
        return new ResourceLocation(GolemOverhaul.MOD_ID, "%s/%s".formatted(name.replace("_golem", ""), name));
    }

    public static ResourceLocation getGlowTexture(EntityType<?> golem) {
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(golem).getPath();
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
