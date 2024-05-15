package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import tech.alexnijjar.golemoverhaul.common.entities.golems.base.BaseGolem;

public class BaseGolemModel<T extends BaseGolem> extends DefaultedEntityGeoModel<T> {

    private final boolean turnsHead;
    private final int maxHeadRotation;
    private final ResourceLocation textureDamaged;
    private final ResourceLocation textureVeryDamaged;

    public BaseGolemModel(
        RegistryEntry<EntityType<T>> golem,
        boolean turnsHead,
        int maxHeadRotation
    ) {
        this(
            BaseGolemRenderer.name(golem),
            BaseGolemRenderer.texture(golem),
            BaseGolemRenderer.name(golem),
            turnsHead,
            maxHeadRotation
        );
    }

    public BaseGolemModel(
        ResourceLocation model,
        ResourceLocation texture,
        ResourceLocation animation,
        boolean turnsHead,
        int maxHeadRotation
    ) {
        this(
            model,
            new ResourceLocation(texture.getNamespace(), "%s_1".formatted(texture.getPath())),
            new ResourceLocation(texture.getNamespace(), "textures/entity/%s_2.png".formatted(texture.getPath())),
            new ResourceLocation(texture.getNamespace(), "textures/entity/%s_3.png".formatted(texture.getPath())),
            animation,
            turnsHead,
            maxHeadRotation
        );
    }

    public BaseGolemModel(
        ResourceLocation model,
        ResourceLocation texture,
        ResourceLocation textureDamaged,
        ResourceLocation textureVeryDamaged,
        ResourceLocation animation,
        boolean turnsHead,
        int maxHeadRotation
    ) {
        super(model, turnsHead);
        this.withAltTexture(texture);
        this.withAltAnimations(animation);
        this.turnsHead = turnsHead;
        this.textureDamaged = textureDamaged;
        this.textureVeryDamaged = textureVeryDamaged;
        this.maxHeadRotation = maxHeadRotation;
    }

    @Override
    public void setCustomAnimations(T golem, long instanceId, AnimationState<T> animationState) {
        if (!turnsHead) return;

        GeoBone head = getAnimationProcessor().getBone("head_rotation");
        if (head == null) return;
        if (head.getChildBones().isEmpty()) return;
        GeoBone headRotation = head.getChildBones().getFirst();
        if (headRotation == null) return;

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        headRotation.setRotX(Mth.clamp(entityData.headPitch(), -maxHeadRotation, maxHeadRotation) * Mth.DEG_TO_RAD);
        headRotation.setRotY(Mth.clamp(entityData.netHeadYaw(), -maxHeadRotation, maxHeadRotation) * Mth.DEG_TO_RAD);
    }

    @Override
    public ResourceLocation getTextureResource(T golem) {
        return switch (golem.getCrackiness()) {
            case NONE, LOW -> super.getTextureResource(golem);
            case MEDIUM -> textureDamaged;
            case HIGH -> textureVeryDamaged;
        };
    }
}
