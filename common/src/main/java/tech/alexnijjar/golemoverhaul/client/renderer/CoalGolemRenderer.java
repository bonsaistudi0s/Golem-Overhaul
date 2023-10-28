package tech.alexnijjar.golemoverhaul.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderer.base.BaseGolemEntityRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.CoalGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CoalGolemRenderer extends BaseGolemEntityRenderer<CoalGolem> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/coal/coal_golem.png");
    public static final ResourceLocation LIT_TEXTURE = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/coal/coal_golem_lit.png");

    public CoalGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<CoalGolem>(
            BuiltInRegistries.ENTITY_TYPE.getKey(ModEntityTypes.COAL_GOLEM.get()))
            .withAltTexture(TEXTURE));

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(CoalGolem animatable) {
                return RenderType.eyes(getTextureLocation(animatable));
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(CoalGolem animatable) {
        return animatable.isLit() ? LIT_TEXTURE : TEXTURE;
    }
}
