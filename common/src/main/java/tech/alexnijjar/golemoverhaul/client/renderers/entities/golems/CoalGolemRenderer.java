package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.utils.ModRenderTypes;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.common.entities.golems.CoalGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CoalGolemRenderer extends BaseGolemRenderer<CoalGolem> {

    public static final ResourceLocation TEXTURE = GolemOverhaul.asResource("textures/entity/coal/coal_golem.png");
    public static final ResourceLocation LIT_TEXTURE = GolemOverhaul.asResource("textures/entity/coal/coal_golem_lit.png");

    public CoalGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.COAL_GOLEM, false, 0));

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(CoalGolem golem) {
                return golem.isLit() ? ModRenderTypes.eyesNoCull(LIT_TEXTURE) : RenderType.entityCutout(TEXTURE);
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CoalGolem golem) {
        return golem.isLit() ? LIT_TEXTURE : TEXTURE;
    }

    @Override
    protected void applyRotations(CoalGolem golem, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        int deathTime = golem.deathTime;
        golem.deathTime = 0;
        super.applyRotations(golem, poseStack, ageInTicks, rotationYaw, partialTick);
        golem.deathTime = deathTime;
    }
}
