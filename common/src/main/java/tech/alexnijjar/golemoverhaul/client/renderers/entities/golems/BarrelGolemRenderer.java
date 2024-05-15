package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.layers.BarrelGolemHeldItemLayer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.BarrelGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class BarrelGolemRenderer extends BaseGolemRenderer<BarrelGolem> {

    public static final ResourceLocation GLOW = new ResourceLocation(GolemOverhaul.MOD_ID, "textures/entity/barrel/barrel_golem_glow.png");

    public BarrelGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.BARREL_GOLEM, true, 10));

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(BarrelGolem golem) {
                return RenderType.eyes(GLOW);
            }
        });

        addRenderLayer(new BarrelGolemHeldItemLayer(this));
    }
}
