package tech.alexnijjar.golemoverhaul.client.renderer.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import tech.alexnijjar.golemoverhaul.common.entities.base.BaseGolem;

public class BaseGolemModel<T extends BaseGolem> extends DefaultedEntityGeoModel<T> {
    private final boolean turnsHead;
    private final int maxHeadRotation;
    private ResourceLocation textureDamaged;
    private ResourceLocation textureVeryDamaged;

    public BaseGolemModel(ResourceLocation assetSubpath, boolean turnsHead, ResourceLocation texture, ResourceLocation animation, int maxHeadRotation) {
        this(
            assetSubpath,
            turnsHead,
            new ResourceLocation(texture.getNamespace(), "%s_0".formatted(texture.getPath())),
            new ResourceLocation(texture.getNamespace(), "textures/entity/%s_1.png".formatted(texture.getPath())),
            new ResourceLocation(texture.getNamespace(), "textures/entity/%s_2.png".formatted(texture.getPath())),
            animation,
            maxHeadRotation
        );
    }

    public BaseGolemModel(ResourceLocation assetSubpath, boolean turnsHead, ResourceLocation texture, ResourceLocation textureDamaged, ResourceLocation textureVeryDamaged, ResourceLocation animation, int maxHeadRotation) {
        super(assetSubpath, turnsHead);
        this.withAltTexture(texture);
        this.withAltAnimations(animation);
        this.turnsHead = turnsHead;
        this.textureDamaged = textureDamaged;
        this.textureVeryDamaged = textureVeryDamaged;
        this.maxHeadRotation = maxHeadRotation;
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (!turnsHead) return;

        CoreGeoBone head = getAnimationProcessor().getBone("head_rotation");
        if (head == null) return;
        if (head.getChildBones().isEmpty()) return;
        CoreGeoBone headRotation = head.getChildBones().get(0);
        if (headRotation == null) return;

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        headRotation.setRotX(Mth.clamp(entityData.headPitch(), -maxHeadRotation, maxHeadRotation) * Mth.DEG_TO_RAD);
        headRotation.setRotY(Mth.clamp(entityData.netHeadYaw(), -maxHeadRotation, maxHeadRotation) * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return switch (animatable.getCrackiness()) {
            case NONE, LOW -> super.getTextureResource(animatable);
            case HIGH -> textureVeryDamaged;
            case MEDIUM -> textureDamaged;
        };
    }
}
