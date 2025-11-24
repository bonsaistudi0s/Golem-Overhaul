package tech.alexnijjar.golemoverhaul.client.renderers.entities.golems;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import tech.alexnijjar.golemoverhaul.GolemOverhaul;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemModel;
import tech.alexnijjar.golemoverhaul.client.renderers.entities.golems.base.BaseGolemRenderer;
import tech.alexnijjar.golemoverhaul.client.utils.ModRenderTypes;
import tech.alexnijjar.golemoverhaul.common.entities.golems.CoalGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

public class CoalGolemRenderer extends BaseGolemRenderer<CoalGolem> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/coal/coal_golem.png");
    public static final ResourceLocation LIT_TEXTURE = ResourceLocation.fromNamespaceAndPath(GolemOverhaul.MOD_ID, "textures/entity/coal/coal_golem_lit.png");

    public CoalGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BaseGolemModel<>(ModEntityTypes.COAL_GOLEM, false, 0));

        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(CoalGolem golem, @Nullable MultiBufferSource bufferSource) {
                return golem.isLit() ? ModRenderTypes.eyesNoCull(LIT_TEXTURE) : RenderType.entityCutout(TEXTURE);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(CoalGolem golem) {
        return golem.isLit() ? LIT_TEXTURE : TEXTURE;
    }

    @Override
    protected void applyRotations(CoalGolem golem, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        int deathTime = golem.deathTime;
        golem.deathTime = 0;
        super.applyRotations(golem, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        golem.deathTime = deathTime;
    }
}
