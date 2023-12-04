package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.common.entities.HoneyGolem;

public class HoneyGolemRenderer extends BaseGolemEntityRenderer<HoneyGolem> {
    public static final ResourceLocation HONEY_GOLEM_TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "honey/honey_golem");
    public static final ResourceLocation HONEY_GOLEM_FULL_TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/honey/honey_golem_full.png");
    public static final ResourceLocation HONEY_GOLEM_FULL_TEXTURE_1 = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/honey/honey_golem_full_1.png");
    public static final ResourceLocation HONEY_GOLEM_FULL_TEXTURE_2 = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/honey/honey_golem_full_2.png");

    public HoneyGolemRenderer(EntityRendererProvider.Context renderManager, EntityType<?> golem) {
        super(renderManager, new BaseGolemModel<>(
            BuiltInRegistries.ENTITY_TYPE.getKey(golem),
            true,
            HONEY_GOLEM_TEXTURE,
            HONEY_GOLEM_ANIMATION, 20) {
            @Override
            public ResourceLocation getTextureResource(HoneyGolem animatable) {
                if (!animatable.isFullOfHoney()) return super.getTextureResource(animatable);
                return switch (animatable.getCrackiness()) {
                    case NONE, LOW -> HONEY_GOLEM_FULL_TEXTURE;
                    case HIGH -> HONEY_GOLEM_FULL_TEXTURE_2;
                    case MEDIUM -> HONEY_GOLEM_FULL_TEXTURE_1;
                };
            }
        });
    }
}
