package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.HoneyGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class HoneyGolemRenderer extends BaseGolemRenderer<HoneyGolem> {

    public static final ResourceLocation FULL_TEXTURE_1 = GolemOverhaul.asResource("textures/entity/honey/honey_golem_full_1.png");
    public static final ResourceLocation FULL_TEXTURE_2 = GolemOverhaul.asResource("textures/entity/honey/honey_golem_full_2.png");
    public static final ResourceLocation FULL_TEXTURE_3 = GolemOverhaul.asResource("textures/entity/honey/honey_golem_full_3.png");

    public HoneyGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.HONEY_GOLEM, true, 20));
    }

    @Override
    public ResourceLocation getTextureLocation(HoneyGolem golem) {
        if (!golem.isFullOfHoney()) return getGeoModel().getTextureResource(golem);
        return switch (golem.getCrackiness()) {
            case NONE, LOW -> FULL_TEXTURE_1;
            case MEDIUM -> FULL_TEXTURE_2;
            case HIGH -> FULL_TEXTURE_3;
        };
    }
}
